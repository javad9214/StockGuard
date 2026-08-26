package ir.yar.anbar.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.Product
import ir.yar.anbar.domain.model.ProductFactory
import ir.yar.anbar.domain.model.ProductSyncResult
import ir.yar.anbar.domain.model.SortOrder
import ir.yar.anbar.domain.model.Subcategory
import ir.yar.anbar.domain.model.type.Money
import ir.yar.anbar.domain.usecase.category.GetSubcategoriesUseCase
import ir.yar.anbar.domain.usecase.product.AddProductUseCase
import ir.yar.anbar.domain.usecase.product.DecreaseStockUseCase
import ir.yar.anbar.domain.usecase.product.DeleteProductUseCase
import ir.yar.anbar.domain.usecase.product.EditProductUseCase
import ir.yar.anbar.domain.usecase.product.GetAllProductUseCase
import ir.yar.anbar.domain.usecase.product.GetProductByIdUseCase
import ir.yar.anbar.domain.usecase.product.GetProductByQueryUseCase
import ir.yar.anbar.domain.usecase.product.IncreaseStockUseCase
import ir.yar.anbar.domain.usecase.product.SyncAllProductsUseCase
import ir.yar.anbar.utils.barcode.BarcodeGenerator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ProductsViewModel"

@HiltViewModel
class ProductsViewModel @Inject constructor(
    // For resolving localized message strings — same pattern as VersionViewModel
    @ApplicationContext private val context: Context,
    private val getProductsUseCase: GetProductByQueryUseCase,
    private val getAllProductUseCase: GetAllProductUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val editProductUseCase: EditProductUseCase,
    private val increaseStockUseCase: IncreaseStockUseCase,
    private val decreaseStockUseCase: DecreaseStockUseCase,
    private val syncAllProductsUseCase: SyncAllProductsUseCase,
    private val getSubcategoriesUseCase: GetSubcategoriesUseCase
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    // Options for the AddProduct subcategory dropdown. Selection is optional
    // decoration, so a failed load just leaves the dropdown empty.
    private val _subcategories = MutableStateFlow<List<Subcategory>>(emptyList())
    val subcategories: StateFlow<List<Subcategory>> get() = _subcategories

    // Inputs of the products pipeline — mutations only update these and the
    // single collector below reacts, so a slow older query can never
    // overwrite a newer one's results
    private val searchQueryFlow = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = searchQueryFlow

    private val sortOrderFlow = MutableStateFlow(SortOrder.DESCENDING)
    val sortOrder: StateFlow<SortOrder> get() = sortOrderFlow

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<Product>> =
        combine(
            // Collapse keystroke-sized bursts into one DB query per pause
            searchQueryFlow.debounce(300),
            sortOrderFlow
        ) { query, sort -> query to sort }
            .flatMapLatest { (query, sort) ->
                val source = if (query.isBlank()) {
                    getAllProductUseCase(sort)
                } else {
                    getProductsUseCase(sort, query)
                }
                source
                    .onStart { _isLoading.value = true }
                    // Caught inside flatMapLatest so a failed query only
                    // completes the inner flow — the pipeline itself survives
                    // and the next input change starts a fresh query
                    .catch { e ->
                        _errorMessage.value = context.getString(
                            R.string.error_failed_to_load_products, e.message
                        )
                        emit(emptyList())
                    }
            }
            .onEach { _isLoading.value = false }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> get() = _selectedProduct

    // Set when a getProductById call fails or finds nothing, so edit-mode
    // screens can show an error state instead of silently degrading into
    // the create form
    private val _selectedProductError = MutableStateFlow<String?>(null)
    val selectedProductError: StateFlow<String?> get() = _selectedProductError

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> get() = _isSyncing

    private val _lastSyncResult = MutableStateFlow<ProductSyncResult?>(null)
    val lastSyncResult: StateFlow<ProductSyncResult?> get() = _lastSyncResult

    // One-shot outcome of saveProduct — the screen collects this to navigate on
    // Success and surface an error Snackbar on Error, instead of navigating
    // before the write has actually completed
    private val _saveEvent = MutableSharedFlow<SaveResult>()
    val saveEvent: SharedFlow<SaveResult> = _saveEvent.asSharedFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> get() = _isSaving

    init {
        loadSubcategories()
    }

    private fun loadSubcategories() {
        viewModelScope.launch {
            try {
                _subcategories.value = getSubcategoriesUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error loading subcategories", e)
            }
        }
    }


    fun saveProduct(
        name: String,
        barcode: String,
        salePrice: String,
        costPrice: String,
        subcategoryId: String,
        localImageUri: String?
    ) {
        if (_isSaving.value) return // a save is already in flight
        // Validate and parse before building the product — invalid input must
        // reject the save, not silently persist a zero price
        if (name.isBlank()) {
            rejectSave(context.getString(R.string.error_product_name_required))
            return
        }
        val saleAmount = Money.parsePositiveOrNull(salePrice)
        if (saleAmount == null) {
            rejectSave(context.getString(R.string.error_sale_price_invalid))
            return
        }
        val costAmount = Money.parsePositiveOrNull(costPrice)
        if (costAmount == null) {
            rejectSave(context.getString(R.string.error_cost_price_invalid))
            return
        }
        val product = _selectedProduct.value
        val newProduct = ProductFactory.createComplete(
            id = product?.id?.value ?: 0,
            name = name,
            barcode = barcode.ifEmpty { BarcodeGenerator.generateBarcodeNumber() },
            price = saleAmount.amount,
            costPrice = costAmount.amount,
            description = product?.description?.value ?: "",
            subcategoryId = subcategoryId.toIntOrNull() ?: product?.subcategoryId?.value ?: 0,
            supplierId = product?.supplierId?.value ?: 0,
            unit = product?.unit?.value ?: "",
            localImageUri = localImageUri,
            remoteImageUrl = product?.image?.remoteUrl,
            initialStock = product?.stock?.value ?: 0,
            minStockLevel = product?.minStockLevel?.value ?: 0,
            maxStockLevel = product?.maxStockLevel?.value ?: 0,
            tags = product?.tags?.value ?: ""
        )
        viewModelScope.launch {
            _isSaving.value = true
            try {
                if (product == null) {
                    addProductUseCase(newProduct)
                } else {
                    editProductUseCase(newProduct)
                }
                // Keep selectedProduct until the write succeeded — the form
                // fields are keyed on it, so clearing early would wipe the
                // user's edits when a failed save leaves them on this screen
                clearSelectedProduct()
                _saveEvent.emit(SaveResult.Success)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error saving product", e)
                _saveEvent.emit(
                    SaveResult.Error(
                        context.getString(R.string.error_failed_to_save_product, e.message)
                    )
                )
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateSortOrder(newOrder: SortOrder) {
        sortOrderFlow.value = newOrder
    }

    fun syncAllProducts() {
        if (_isSyncing.value) return // a sync pass is already running
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                _lastSyncResult.value = syncAllProductsUseCase()
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            deleteProductUseCase(product)
        }
    }

    fun editProduct(product: Product) {
        if (_isSaving.value) return // a save is already in flight
        viewModelScope.launch {
            _isSaving.value = true
            try {
                editProductUseCase.invoke(product)
                _saveEvent.emit(SaveResult.Success)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error editing product", e)
                _saveEvent.emit(
                    SaveResult.Error(
                        context.getString(R.string.error_failed_to_edit_product, e.message)
                    )
                )
            } finally {
                _isSaving.value = false
            }
        }
    }

    // Stock and product writes re-emit through the Room flow backing
    // [products], so the list refreshes without a manual reload

    // Increase stock
    fun increaseStock(product: Product) {
        viewModelScope.launch {
            increaseStockUseCase(product)
        }
    }

    // Decrease stock
    fun decreaseStock(product: Product) {
        viewModelScope.launch {
            decreaseStockUseCase(product)
        }
    }

    fun getProductById(productId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedProductError.value = null
            try {
                val product = getProductByIdUseCase(productId)
                if (product == null) {
                    _selectedProduct.value = null
                    _selectedProductError.value =
                        context.getString(R.string.error_product_not_found)
                } else {
                    _selectedProduct.value = product
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error loading product $productId", e)
                _selectedProductError.value = context.getString(
                    R.string.error_failed_to_load_product, e.message
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Validation failures ride the same one-shot channel as save errors, so
    // the form stays up and the user gets a snackbar instead of a silent
    // zero-value fallback
    private fun rejectSave(message: String) {
        viewModelScope.launch {
            _saveEvent.emit(SaveResult.Error(message))
        }
    }

    // Public so entry points (e.g. AddProduct's add-new flow) can reset a
    // stale selection left over from a previous edit on the same instance
    fun clearSelectedProduct() {
        _selectedProduct.value = null
        _selectedProductError.value = null
    }
}

sealed interface SaveResult {
    data object Success : SaveResult
    data class Error(val message: String) : SaveResult
}
