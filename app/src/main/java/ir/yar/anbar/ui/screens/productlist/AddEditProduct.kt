package ir.yar.anbar.ui.screens.productlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.Subcategory
import ir.yar.anbar.domain.model.UnitOfMeasure
import ir.yar.anbar.domain.model.type.Money
import ir.yar.anbar.ui.components.image.ImagePickerBox
import ir.yar.anbar.ui.components.barcodescanner.CompactBarcodeScanner
import ir.yar.anbar.ui.components.util.ConfirmyHost
import ir.yar.anbar.ui.components.util.ConfirmyType
import ir.yar.anbar.ui.components.util.SnackyDuration
import ir.yar.anbar.ui.components.util.SnackyHost
import ir.yar.anbar.ui.components.util.SnackyType
import ir.yar.anbar.ui.components.util.rememberConfirmyHostState
import ir.yar.anbar.ui.components.util.rememberSnackyHostState
import ir.yar.anbar.ui.screens.component.CurrencyIcon
import ir.yar.anbar.ui.theme.BKoodak
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.ui.theme.color.costPrice
import ir.yar.anbar.ui.theme.color.customError
import ir.yar.anbar.ui.theme.color.salePrice
import ir.yar.anbar.ui.theme.color.success
import ir.yar.anbar.ui.viewmodels.ProductsViewModel
import ir.yar.anbar.ui.viewmodels.SaveResult
import ir.yar.anbar.utils.barcode.BarcodeSoundPlayer
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.price.PriceValidator
import ir.yar.anbar.utils.price.ThousandSeparatorTransformation
import kotlinx.coroutines.delay

// Barcode auto-lookup (add mode): real-world barcodes are at least EAN-8,
// and typing bursts collapse into one call after a short pause
private const val MIN_LOOKUP_BARCODE_LENGTH = 8
private const val BARCODE_LOOKUP_DEBOUNCE_MS = 700L

@Composable
fun AddProduct(
    initialBarcode: String? = null,
    productId: Long? = null,
    onSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    productsViewModel: ProductsViewModel = hiltViewModel(),
) {


    // Fetch product if editing; explicitly clear any stale selection when
    // entering the add-new flow, in case this VM instance previously loaded
    // another product (edit A → add new on a shared instance)
    LaunchedEffect(productId) {
        if (productId != null) {
            productsViewModel.getProductById(productId)
        } else {
            productsViewModel.clearSelectedProduct()
        }
    }

    val product by productsViewModel.selectedProduct.collectAsState()
    val productLoadError by productsViewModel.selectedProductError.collectAsState()
    val isLoading by productsViewModel.isLoading.collectAsState()
    val isSaving by productsViewModel.isSaving.collectAsState()
    val isBarcodeLookupLoading by productsViewModel.isBarcodeLookupLoading.collectAsState()
    val barcodeLookupResult by productsViewModel.barcodeLookupResult.collectAsState()
    val subcategories by productsViewModel.subcategories.collectAsState()
    val defaultUnit by productsViewModel.defaultUnit.collectAsState()
    val visibleUnits by productsViewModel.visibleUnits.collectAsState()
    val snackyHostState = rememberSnackyHostState()
    val confirmyHostState = rememberConfirmyHostState()

    // Track which fields the user has interacted with, so inline errors only
    // appear after interaction instead of flagging an untouched empty form
    var nameTouched by remember { mutableStateOf(false) }
    var costTouched by remember { mutableStateOf(false) }
    var saleTouched by remember { mutableStateOf(false) }

    // Any user modification of the form — gates the discard-changes guard
    var isDirty by remember { mutableStateOf(false) }

    // Saving is async — navigate only once the save actually succeeded,
    // otherwise a failed write would strand the user with no feedback
    LaunchedEffect(Unit) {
        productsViewModel.saveEvent.collect { result ->
            when (result) {
                is SaveResult.Success -> onSaved()
                is SaveResult.Error -> snackyHostState.show(
                    message = result.message,
                    type = SnackyType.ERROR,
                    duration = SnackyDuration.LONG
                )
            }
        }
    }

    // Initialize form fields with product data or defaults
    var name by remember(product) {
        mutableStateOf(product?.name?.value ?: "")
    }

    var barcode by remember(product, initialBarcode) {
        mutableStateOf(initialBarcode ?: product?.barcode?.value ?: "")
    }

    // Prices are stored in cents — the form works in display units, so the
    // prefill must convert or an edited product shows 100x its real price
    var salePrice by remember(product) {
        mutableStateOf(product?.price?.toDisplayAmount()?.toString() ?: "")
    }

    var costPrice by remember(product) {
        mutableStateOf(product?.costPrice?.toDisplayAmount()?.toString() ?: "")
    }

    var subcategoryId by remember(product) {
        mutableStateOf(product?.subcategoryId?.value?.toString() ?: "")
    }

    // Initial stock in the chosen unit — prefilled with the current stock in
    // edit mode; blank keeps the existing stock (0 for a new product)
    var initialStock by remember(product) {
        mutableStateOf(product?.stock?.value?.toString() ?: "")
    }

    // The wire value is the exact server enum name. Edits start from the
    // product's own unit; new products pre-select the persisted default
    // (PIECE out of the box), which may arrive one frame after first
    // composition — hence the key re-initializing the selection
    var selectedUnit by remember(product, defaultUnit) {
        mutableStateOf(
            UnitOfMeasure.fromName(product?.unit?.value)
                ?: UnitOfMeasure.fromName(defaultUnit)
        )
    }

    val isEditMode = product != null

    // Add mode only — typing or scanning a barcode looks it up against the
    // server's Daryamart catalog. The delay doubles as the debounce:
    // restarting this effect on every barcode change cancels the previous
    // wait, so keystroke bursts collapse into one call
    LaunchedEffect(barcode, isEditMode) {
        // a changed barcode invalidates the previously looked-up image
        remoteImageUrl = null
        if (isEditMode || barcode.length < MIN_LOOKUP_BARCODE_LENGTH) return@LaunchedEffect
        delay(BARCODE_LOOKUP_DEBOUNCE_MS)
        productsViewModel.lookupBarcode(barcode)
    }

    val barcodeLookupFailedMessage = stringResource(R.string.barcode_lookup_failed)

    // Auto-fill from a finished lookup: name → product name, sellPrice →
    // sale price, imageUrl → the image preview. A result for a barcode the
    // user has already changed away from is stale and ignored; failures
    // surface the server's fa message (e.g. «محصولی با این بارکد یافت نشد»)
    // so the user knows why nothing was filled
    LaunchedEffect(barcodeLookupResult) {
        val result = barcodeLookupResult ?: return@LaunchedEffect
        if (isEditMode || result.barcode != barcode) return@LaunchedEffect
        val found = result.product
        if (found != null) {
            found.name?.takeIf { it.isNotBlank() }?.let { name = it }
            // sellPrice arrives in display units — the same unit the field
            // itself holds, so no cents conversion is needed
            found.sellPrice?.let { salePrice = it.toString() }
            // show the looked-up image only when the user hasn't picked one —
            // their own photo must win in both preview and save
            if (imageUri == null) {
                found.imageUrl?.takeIf { it.isNotBlank() }?.let { remoteImageUrl = it }
            }
            isDirty = true
        } else {
            snackyHostState.show(
                message = result.errorMessage ?: barcodeLookupFailedMessage,
                type = SnackyType.ERROR,
                duration = SnackyDuration.LONG
            )
        }
    }

    // Single parse per recomposition — feeds the button gate, the inline
    // field errors, and the live profit hint
    val costAmount = Money.parsePositiveOrNull(costPrice)
    val saleAmount = Money.parsePositiveOrNull(salePrice)

    // Mirrors saveProduct's validation rules (blank name, non-positive
    // prices) so the button is disabled for input the VM would reject
    val isFormValid = name.isNotBlank() && saleAmount != null && costAmount != null
    val isNameError = nameTouched && name.isBlank()
    val isCostError = costTouched && costAmount == null
    val isSaleError = saleTouched && saleAmount == null

    var imageUri by remember(product) {
        mutableStateOf(product?.image?.displayPath?.toUri())
    }

    // Server image from the barcode lookup (add mode) — shown in the image
    // preview until the user picks their own photo, and saved as the
    // product's remote image (no local copy or upload involved)
    var remoteImageUrl by remember(product) { mutableStateOf<String?>(null) }

    // Leaving with unsaved edits — via the top-bar close or the system back
    // gesture — asks for confirmation instead of silently discarding them
    val discardMessage = stringResource(R.string.discard_changes_message)
    val discardText = stringResource(R.string.discard)
    val cancelText = stringResource(R.string.cancel)
    val requestClose = {
        if (isSaving) {
            // A save is in flight — ignore close requests so it can't be abandoned
        } else if (isDirty) {
            confirmyHostState.show(
                message = discardMessage,
                type = ConfirmyType.ERROR,
                confirmText = discardText,
                cancelText = cancelText,
                onConfirm = onNavigateBack
            )
        } else {
            onNavigateBack()
        }
    }
    BackHandler(enabled = isDirty && !isSaving) { requestClose() }

    // A failed edit-mode load must not degrade into the create form —
    // saving that would silently duplicate the product
    if (productId != null) {
        productLoadError?.let { loadError ->
            ProductLoadErrorState(
                message = loadError,
                onRetry = { productsViewModel.getProductById(productId) },
                onNavigateBack = onNavigateBack
            )
            return
        }
    }

    // Edit mode fetches asynchronously — hold the form back so the screen
    // doesn't flash an empty create form before the data arrives
    if (productId != null && isLoading) {
        ProductLoadingState(onNavigateBack = onNavigateBack)
        return
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header with title and close button
            AddProductTopBar(
                isEditMode = isEditMode,
                onNavigateBack = requestClose
            )

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_1)))

            Column(
                modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
            ) {
                ProductNameField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameTouched = true
                        isDirty = true
                    },
                    isError = isNameError
                )
                Spacer(modifier = Modifier.height(16.dp))

                BarcodeField(
                    value = barcode,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 13) {
                            barcode = newValue
                            isDirty = true
                        }
                    },
                    isLookingUp = isBarcodeLookupLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Initial stock and its unit of measure sit side by side —
                // the number stays short so both fit even on narrow phones
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_4))
                ) {
                    InitialStockField(
                        value = initialStock,
                        onValueChange = { newValue ->
                            // Digits only, at most 7 — StockQuantity caps at 1,000,000
                            if (newValue.all { it.isDigit() } && newValue.length <= 7) {
                                initialStock = newValue
                                isDirty = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    UnitDropdownField(
                        selected = selectedUnit,
                        onSelect = {
                            selectedUnit = it
                            isDirty = true
                        },
                        visibleUnits = visibleUnits,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cost and sale price read as a comparison on wide screens,
                // stacked full-width on phones
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    if (maxWidth >= 480.dp) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_4))
                        ) {
                            PriceField(
                                value = costPrice,
                                onValueChange = {
                                    costPrice = it.replace(",", "")
                                    costTouched = true
                                    isDirty = true
                                },
                                label = R.string.cost_price,
                                iconRes = R.drawable.input_circle_24px,
                                colorScheme = MaterialTheme.colorScheme.costPrice,
                                modifier = Modifier.weight(1f),
                                isError = isCostError
                            )
                            PriceField(
                                value = salePrice,
                                onValueChange = {
                                    salePrice = it.replace(",", "")
                                    saleTouched = true
                                    isDirty = true
                                },
                                label = R.string.sale_price,
                                iconRes = R.drawable.output_circle_24px,
                                colorScheme = MaterialTheme.colorScheme.salePrice,
                                modifier = Modifier.weight(1f),
                                isError = isSaleError
                            )
                        }
                    } else {
                        // BoxWithConstraints has no implicit vertical layout —
                        // the stacked fields must go in a Column
                        Column {
                            PriceField(
                                value = costPrice,
                                onValueChange = {
                                    costPrice = it.replace(",", "")
                                    costTouched = true
                                    isDirty = true
                                },
                                label = R.string.cost_price,
                                iconRes = R.drawable.input_circle_24px,
                                colorScheme = MaterialTheme.colorScheme.costPrice,
                                isError = isCostError
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PriceField(
                                value = salePrice,
                                onValueChange = {
                                    salePrice = it.replace(",", "")
                                    saleTouched = true
                                    isDirty = true
                                },
                                label = R.string.sale_price,
                                iconRes = R.drawable.output_circle_24px,
                                colorScheme = MaterialTheme.colorScheme.salePrice,
                                isError = isSaleError
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Live margin feedback once both prices parse — green when
                // profitable, error-colored when selling below cost
                if (costAmount != null && saleAmount != null) {
                    val profit = saleAmount.amount - costAmount.amount
                    // Money.amount is Long — divide as Double or the margin truncates
                    val marginPercent = profit * 100.0 / costAmount.amount
                    Text(
                        text = stringResource(
                            R.string.profit_margin_hint,
                            PriceValidator.formatPrice(profit),
                            String.format("%.1f", marginPercent)
                        ),
                        fontFamily = BKoodak,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimenTextSize(R.dimen.text_size_sm),
                        color = if (profit >= 0) {
                            MaterialTheme.colorScheme.success
                        } else {
                            MaterialTheme.colorScheme.customError
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (saleAmount.amount <= costAmount.amount) {
                        Spacer(modifier = Modifier.height(dimen(R.dimen.space_1)))
                        Text(
                            text = stringResource(R.string.warning_selling_below_cost),
                            fontFamily = BKoodak,
                            fontSize = dimenTextSize(R.dimen.text_size_sm),
                            color = MaterialTheme.colorScheme.customError,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                ImagePickerBox(
                    // Coil loads the picked local uri and the server's
                    // https image alike
                    imageUri = imageUri ?: remoteImageUrl?.toUri(),
                    onImageSelected = {
                        imageUri = it
                        remoteImageUrl = null
                        isDirty = true
                    },
                    onImageRemoved = {
                        imageUri = null
                        remoteImageUrl = null
                        isDirty = true
                    }
                )

                // Reserve space so the last item isn't hidden behind the fixed SaveButton
                Spacer(modifier = Modifier.height(88.dp))
            }
        }

        // Save Button
        SaveButton(
            isEditMode = isEditMode,
            isSaving = isSaving,
            enabled = isFormValid,
            onSave = {
                productsViewModel.saveProduct(
                    name = name,
                    barcode = barcode,
                    salePrice = salePrice,
                    costPrice = costPrice,
                    subcategoryId = subcategoryId,
                    localImageUri = imageUri?.toString(),
                    initialStock = initialStock,
                    unit = selectedUnit?.name,
                    remoteImageUrl = remoteImageUrl
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Snackbar for save errors
        SnackyHost(hostState = snackyHostState)

        // Discard-changes confirmation
        ConfirmyHost(hostState = confirmyHostState)
    }
}

@Composable
private fun ProductLoadingState(
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AddProductTopBar(
            isEditMode = true,
            onNavigateBack = onNavigateBack
        )

        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ProductLoadErrorState(
    message: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AddProductTopBar(
            isEditMode = true,
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_lg),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))
            OutlinedButton(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
fun AddProductTopBar(
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(
                id = if (isEditMode) R.string.edit_product_title else R.string.add_product_title
            ),
            fontFamily = Beirut_Medium,
            fontSize = dimenTextSize(R.dimen.text_size_xl)
        )

        // Close icon
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(dimen(R.dimen.space_2))
                .size(dimen(R.dimen.size_lg))
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.08f))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
private fun ProductNameField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                stringResource(R.string.product_name),
                fontFamily = BKoodak,
                fontWeight = FontWeight.Bold
            )
        },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(stringResource(R.string.error_product_name_required))
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = BKoodak,
            fontWeight = FontWeight.Bold,
            fontSize = dimenTextSize(R.dimen.text_size_md)
        )
    )
}


@Composable
private fun BarcodeField(
    value: String,
    onValueChange: (String) -> Unit,
    isLookingUp: Boolean = false
) {

    // Context for MediaPlayer
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = 182.dp
            )
    ) {
        CompactBarcodeScanner(
            onBarcodeDetected = { detectedBarcode ->

                // Play barcode success sound
                BarcodeSoundPlayer.playBarcodeSuccessSound(context)

                onValueChange(detectedBarcode)

            },
            startPaused = true,
            modifier = Modifier
                .padding(
                    horizontal = dimen(R.dimen.space_2),
                    vertical = dimen(R.dimen.space_2)
                )
                .align(Alignment.TopCenter),
            cardRadius = dimen(R.dimen.radius_md)
        )

    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                stringResource(R.string.barcode_optional),
                fontFamily = BKoodak,
                fontWeight = FontWeight.Bold
            )
        },
        trailingIcon = {
            // Swapped for a spinner while the Daryamart lookup is running,
            // so the auto-fill that follows doesn't feel like it came from nowhere
            if (isLookingUp) {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimen(R.dimen.size_sm)),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.barcode_24px),
                    contentDescription = stringResource(R.string.barcode_optional),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = BKoodak,
            fontWeight = FontWeight.Bold,
            fontSize = dimenTextSize(R.dimen.text_size_md)
        )
    )
}

@Composable
private fun InitialStockField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                stringResource(R.string.initial_stock),
                fontFamily = BKoodak,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = BKoodak,
            fontWeight = FontWeight.Bold,
            fontSize = dimenTextSize(R.dimen.text_size_md)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdownField(
    selected: UnitOfMeasure?,
    onSelect: (UnitOfMeasure?) -> Unit,
    visibleUnits: Set<String> = UnitOfMeasure.values().map { it.name }.toSet(),
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        // faName is the display label; the enum name is what gets saved
        OutlinedTextField(
            value = selected?.faName ?: "",
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    stringResource(R.string.unit_optional),
                    fontFamily = BKoodak,
                    fontWeight = FontWeight.Bold
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = BKoodak,
                fontWeight = FontWeight.Bold,
                fontSize = dimenTextSize(R.dimen.text_size_md)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Only offer clearing once a unit is actually selected
            if (selected != null) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.none)) },
                    onClick = {
                        onSelect(null)
                        expanded = false
                    }
                )
            }
            // Offer the visible units (settings-controlled), always keeping
            // the current selection listed so it can still be changed away
            // from even when hidden
            UnitOfMeasure.values()
                .filter { it.name in visibleUnits || it == selected }
                .forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit.faName) },
                        onClick = {
                            onSelect(unit)
                            expanded = false
                        }
                    )
                }
        }
    }
}

@Composable
 fun PriceField(
    value: String,
    onValueChange: (String) -> Unit,
    label: Int,
    iconRes: Int,
    colorScheme: Color,
    modifier: Modifier = Modifier,
    // Opt-in: only callers that actually drive focus (e.g. the price
    // bottom sheet's auto-focus) need to supply one
    focusRequester: FocusRequester? = null,
    isError: Boolean = false
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        trailingIcon = {
            CurrencyIcon(
                contentDescription = stringResource(R.string.currency_icon),
                tint = colorScheme,
                modifier = Modifier.size(dimen(R.dimen.size_sm))
            )
        },
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .size(dimen(R.dimen.size_xs))
                        .rotate(90f),
                    painter = painterResource(id = iconRes),
                    contentDescription = stringResource(label),
                    tint = colorScheme
                )
                Spacer(modifier = Modifier.width(dimen(R.dimen.space_2)))
                Text(
                    stringResource(label),
                    fontFamily = BKoodak,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme
                )
            }
        },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(stringResource(R.string.error_enter_valid_price))
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (focusRequester != null) {
                    Modifier.focusRequester(focusRequester)
                } else {
                    Modifier
                }
            ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        visualTransformation = ThousandSeparatorTransformation(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = BKoodak,
            fontWeight = FontWeight.Bold,
            fontSize = dimenTextSize(R.dimen.text_size_md)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubcategoryDropdownField(
    subcategories: List<Subcategory>,
    selectedId: String,
    fallbackName: String?,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Show the selected option's name; in edit mode an unloaded/missing
    // subcategory falls back to the name stored on the product
    val selectedName = subcategories.find { it.id.toString() == selectedId }?.name
        ?: fallbackName.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    stringResource(R.string.subcategory_optional),
                    fontFamily = BKoodak,
                    fontWeight = FontWeight.Bold
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = BKoodak,
                fontWeight = FontWeight.Bold,
                fontSize = dimenTextSize(R.dimen.text_size_md)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Only offer clearing once something is actually selected
            if (selectedId.isNotEmpty()) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.none)) },
                    onClick = {
                        onSelect("")
                        expanded = false
                    }
                )
            }
            subcategories.forEach { subcategory ->
                DropdownMenuItem(
                    text = { Text(subcategory.name) },
                    onClick = {
                        onSelect(subcategory.id.toString())
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SaveButton(
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    onSave: () -> Unit,
    enabled: Boolean = true,
    isSaving: Boolean = false
) {
    Button(
        onClick = onSave,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimen(R.dimen.space_6), vertical = dimen(R.dimen.space_4)),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        enabled = enabled && !isSaving
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(dimen(R.dimen.size_xs)),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(dimen(R.dimen.space_2)))
        }
        Text(
            modifier = Modifier.padding(vertical = dimen(R.dimen.space_1)),
            text = stringResource(
                id = if (isEditMode) R.string.save_changes_button else R.string.add_product_button
            ),
            fontFamily = Beirut_Medium,
            fontWeight = FontWeight.Bold,
            fontSize = dimenTextSize(R.dimen.text_size_lg)
        )
    }
}
