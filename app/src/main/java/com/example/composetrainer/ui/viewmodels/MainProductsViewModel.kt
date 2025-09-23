package com.example.composetrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetrainer.data.remote.dto.PagedResponseDto
import com.example.composetrainer.domain.model.Product
import com.example.composetrainer.domain.usecase.product.AddProductUseCase
import com.example.composetrainer.domain.usecase.servermainproduct.AddNewProductToMainServerUseCase
import com.example.composetrainer.domain.usecase.servermainproduct.GetAllMainProductsUseCase
import com.example.composetrainer.domain.usecase.servermainproduct.GetSearchedMainProductsUseCase
import com.example.composetrainer.domain.util.Resource
import com.example.composetrainer.ui.screens.productlist.ProductsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainProductsViewModel @Inject constructor(
    private val getAllMainProductsUseCase: GetAllMainProductsUseCase,
    private val getSearchedMainProductsUseCase: GetSearchedMainProductsUseCase,
    private val addNewProductToMainServerUseCase: AddNewProductToMainServerUseCase,
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _createState = MutableStateFlow<Resource<Long>>(Resource.Loading())
    val createState: StateFlow<Resource<Long>> = _createState

    private val _updateState = MutableStateFlow<Resource<Product>>(Resource.Loading())
    val updateState: StateFlow<Resource<Product>> = _updateState

    private val _deleteState = MutableStateFlow<Resource<Product>>(Resource.Loading())
    val deleteState: StateFlow<Resource<Product>> = _deleteState


    init {
        loadProducts(reset = true)
    }

    private fun loadProducts(reset: Boolean = false) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val page = if (reset) 0 else currentState.currentPage
            val query = _searchQuery.value

            // Update loading state
            _uiState.update { state ->
                state.copy(
                    isLoading = reset,
                    isLoadingMore = !reset,
                    error = null
                )
            }

            // Choose the appropriate use case based on query
            val resourceFlow = if (query.isBlank()) {
                getAllMainProductsUseCase(page)
            } else {
                getSearchedMainProductsUseCase(query, page)
            }

            // Handle the resource response
            resourceFlow.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Loading state already set above
                    }

                    is Resource.Success -> {
                        handleSuccessResponse(resource.data, reset, page)
                    }

                    is Resource.Error -> {
                        handleErrorResponse(resource.message)
                    }
                }
            }
        }
    }

    private fun handleSuccessResponse(pagedResponse: PagedResponseDto<Product>, reset: Boolean, page: Int) {
        _uiState.update { state ->
            state.copy(
                products = if (reset) {
                    pagedResponse.content
                } else {
                    state.products + pagedResponse.content
                },
                isLoading = false,
                isLoadingMore = false,
                error = null,
                hasMorePages = !pagedResponse.last,
                currentPage = if (pagedResponse.content.isNotEmpty()) {
                    page + 1
                } else {
                    state.currentPage
                }
            )
        }
    }

    private fun handleErrorResponse(errorMessage: String?) {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                isLoadingMore = false,
                error = errorMessage
            )
        }
    }

    fun loadMoreProducts() {
        val currentState = _uiState.value
        if (!currentState.isLoading &&
            !currentState.isLoadingMore &&
            currentState.hasMorePages
        ) {
            loadProducts(reset = false)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        loadProducts(reset = true)
    }

    fun retry() {
        loadProducts(reset = true)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun addProductToLocalDatabase(product: Product) {
        viewModelScope.launch {
            addProductUseCase.invoke(product)
        }
    }

    fun addNewProductToRemote(product: Product) {
        viewModelScope.launch {
            addNewProductToMainServerUseCase(product).collect{ resource ->
                _createState.value = resource
            }
        }
    }

}