package ir.yar.anbar.ui.screens.productlist

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.UnitOfMeasure
import ir.yar.anbar.domain.model.type.Money
import ir.yar.anbar.domain.voice.VoiceField
import ir.yar.anbar.domain.voice.VoiceFormCommandParser
import ir.yar.anbar.ui.components.image.ImagePickerBox
import ir.yar.anbar.ui.components.barcodescanner.CompactBarcodeScanner
import ir.yar.anbar.ui.components.safeBottomImePadding
import ir.yar.anbar.ui.components.util.ConfirmyHost
import ir.yar.anbar.ui.components.util.ConfirmyType
import ir.yar.anbar.ui.components.util.SnackyDuration
import ir.yar.anbar.ui.components.util.SnackyHost
import ir.yar.anbar.ui.components.util.SnackyType
import ir.yar.anbar.ui.components.util.rememberConfirmyHostState
import ir.yar.anbar.ui.components.util.rememberSnackyHostState
import ir.yar.anbar.ui.components.voice.VoiceListeningDialog
import ir.yar.anbar.ui.components.voice.VoiceState
import ir.yar.anbar.ui.components.voice.rememberVoiceInputController
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
import kotlinx.coroutines.launch

// Barcode auto-lookup (add mode): real-world barcodes are at least EAN-8,
// and typing bursts collapse into one call after a short pause
private const val MIN_LOOKUP_BARCODE_LENGTH = 8
private const val BARCODE_LOOKUP_DEBOUNCE_MS = 700L

// Cost and sale price sit side by side above this width, stacked below it
private val WideLayoutBreakpoint = 480.dp

// Room under the scroll content for the fixed bottom SaveButton (button
// height + its vertical padding) so the last field is never hidden
private val SaveButtonReserve = 88.dp

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
    val defaultUnit by productsViewModel.defaultUnit.collectAsState()
    val visibleUnits by productsViewModel.visibleUnits.collectAsState()
    val snackyHostState = rememberSnackyHostState()
    val confirmyHostState = rememberConfirmyHostState()
    // SnackyHostState.show is suspend — non-suspend callbacks (permission
    // results, clicks) launch snackbars through this scope
    val scope = rememberCoroutineScope()

    // ── Voice input (fa-IR): the top-bar mic recognizes one utterance and
    // VoiceFormCommandParser routes its keyword-anchored segments into the
    // form fields. The controller is shared with the top bar through
    // composition scope, and its recognizer is disposed on leaving it
    val context = LocalContext.current
    val voiceInput = rememberVoiceInputController()
    val voiceState by voiceInput.state.collectAsState()
    val voicePartial by voiceInput.partialTranscript.collectAsState()

    // Mic tap starts listening when permission is already granted;
    // otherwise the request launches first and the grant callback starts
    val voicePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            voiceInput.start()
        } else {
            scope.launch {
                snackyHostState.show(
                    message = context.getString(R.string.error_voice_permission_denied),
                    type = SnackyType.ERROR,
                    duration = SnackyDuration.SHORT
                )
            }
        }
    }
    val startVoiceInput = {
        val micGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (micGranted) {
            voiceInput.start()
        } else {
            voicePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Keyboard "Next" chain: barcode → initial stock → cost price → sale
    // price (the read-only unit dropdown is skipped; sale price ends the
    // chain with Done and clears focus)
    val focusManager = LocalFocusManager.current
    val stockFocus = remember { FocusRequester() }
    val costFocus = remember { FocusRequester() }
    val saleFocus = remember { FocusRequester() }

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

    // Voice results fill the form: field labels anchor the segments (see
    // VoiceFormCommandParser), and recognition alternates get a second
    // chance when the best candidate mis-hears a label. Each terminal state
    // is a fresh emission, so back-to-back recognitions still re-announce
    LaunchedEffect(voiceState) {
        when (val vs = voiceState) {
            is VoiceState.Result -> {
                val fills = VoiceFormCommandParser.parseFirstMatch(vs.candidates)
                val applied = mutableListOf<VoiceField>()
                fills[VoiceField.NAME]?.let {
                    name = it
                    nameTouched = true
                    isDirty = true
                    applied += VoiceField.NAME
                }
                // Mirrors the typing rule for the field: at most 7 digits
                // (StockQuantity caps at 1,000,000)
                fills[VoiceField.INITIAL_STOCK]?.takeIf { it.length <= 7 }?.let {
                    initialStock = it
                    isDirty = true
                    applied += VoiceField.INITIAL_STOCK
                }
                fills[VoiceField.COST_PRICE]?.let {
                    costPrice = it
                    costTouched = true
                    isDirty = true
                    applied += VoiceField.COST_PRICE
                }
                fills[VoiceField.SALE_PRICE]?.let {
                    salePrice = it
                    saleTouched = true
                    isDirty = true
                    applied += VoiceField.SALE_PRICE
                }
                if (applied.isEmpty()) {
                    snackyHostState.show(
                        message = context.getString(R.string.error_voice_not_understood),
                        type = SnackyType.ERROR,
                        duration = SnackyDuration.SHORT
                    )
                } else {
                    snackyHostState.show(
                        message = context.getString(
                            R.string.voice_fields_filled,
                            applied.joinToString("، ") { context.getString(it.labelRes()) }
                        ),
                        type = SnackyType.SUCCESS,
                        duration = SnackyDuration.LONG
                    )
                }
            }
            is VoiceState.Error -> snackyHostState.show(
                message = voiceErrorText(context, vs.code),
                type = SnackyType.ERROR,
                duration = SnackyDuration.SHORT
            )
            VoiceState.Idle, VoiceState.Listening -> Unit
        }
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

    var imageUri by remember(product) {
        mutableStateOf(product?.image?.displayPath?.toUri())
    }

    // Server image from the barcode lookup (add mode) — shown in the image
    // preview until the user picks their own photo, and saved as the
    // product's remote image (no local copy or upload involved)
    var remoteImageUrl by remember(product) { mutableStateOf<String?>(null) }

    // Catalog link from the same lookup — when the server knows the barcode,
    // saving adopts that catalog product server-side instead of creating an
    // unlinked custom row. Cleared together with the lookup data whenever
    // the barcode changes, so a stale link can never be adopted
    var catalogProductId by remember(product) { mutableStateOf<Long?>(null) }

    // Add mode only — typing or scanning a barcode looks it up against the
    // server's Daryamart catalog. The delay doubles as the debounce:
    // restarting this effect on every barcode change cancels the previous
    // wait, so keystroke bursts collapse into one call
    LaunchedEffect(barcode, isEditMode) {
        // a changed barcode invalidates the previously looked-up image and link
        remoteImageUrl = null
        catalogProductId = null
        if (isEditMode || barcode.length < MIN_LOOKUP_BARCODE_LENGTH) return@LaunchedEffect
        delay(BARCODE_LOOKUP_DEBOUNCE_MS)
        productsViewModel.lookupBarcode(barcode)
    }

    val barcodeLookupFailedMessage = stringResource(R.string.barcode_lookup_failed)

    // Auto-fill from a finished lookup: name → product name, sellPrice →
    // sale price, imageUrl → the image preview, catalogId → the save-time
    // adopt link. A result for a barcode the
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
            catalogProductId = found.catalogId
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

    // While the listening dialog is up, back cancels the session instead
    // of triggering the discard-changes guard; composed after it so this
    // handler wins while enabled
    BackHandler(enabled = voiceState is VoiceState.Listening) { voiceInput.cancel() }

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
                // Viewport ends above the navigation bar / keyboard so the
                // last field scrolls clear of both
                .safeBottomImePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header with title and close button
            AddProductTopBar(
                isEditMode = isEditMode,
                onNavigateBack = requestClose,
                // Null hides the mic on devices without any speech service
                onMicClick = if (voiceInput.isAvailable()) startVoiceInput else null,
                isListening = voiceState is VoiceState.Listening
            )

            Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

            Column(
                modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4))
            ) {
                // ── Identity: what the product is ──────────────────────
                // Name, barcode (scanner + field read as one control), and
                // the photo belong together — tight 12dp gaps inside the
                // group, a generous 24dp before the next one
                ProductNameField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameTouched = true
                        isDirty = true
                    },
                    isError = isNameError
                )
                Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))

                BarcodeField(
                    value = barcode,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 13) {
                            barcode = newValue
                            isDirty = true
                        }
                    },
                    isLookingUp = isBarcodeLookupLoading,
                    onNext = { stockFocus.requestFocus() }
                )
                Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))

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

                // ── Inventory: how it's counted ────────────────────────
                Spacer(modifier = Modifier.height(dimen(R.dimen.space_6)))

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
                        focusRequester = stockFocus,
                        onNext = { costFocus.requestFocus() },
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

                // ── Pricing: what it costs and earns ───────────────────
                Spacer(modifier = Modifier.height(dimen(R.dimen.space_6)))

                // Cost and sale price read as a comparison on wide screens,
                // stacked full-width on phones
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    if (maxWidth >= WideLayoutBreakpoint) {
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
                                focusRequester = costFocus,
                                imeAction = ImeAction.Next,
                                onImeAction = { saleFocus.requestFocus() },
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
                                focusRequester = saleFocus,
                                imeAction = ImeAction.Done,
                                onImeAction = { focusManager.clearFocus() },
                                isError = isSaleError
                            )
                        }
                    } else {
                        // BoxWithConstraints has no implicit vertical layout —
                        // the stacked fields must go in a Column
                        Column(verticalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_3))) {
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
                                focusRequester = costFocus,
                                imeAction = ImeAction.Next,
                                onImeAction = { saleFocus.requestFocus() },
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
                                focusRequester = saleFocus,
                                imeAction = ImeAction.Done,
                                onImeAction = { focusManager.clearFocus() },
                                isError = isSaleError
                            )
                        }
                    }
                }

                // Live margin feedback once both prices parse — attached to
                // the price pair with the same tight group gap
                if (costAmount != null && saleAmount != null) {
                    // Money.amount is Long — divide as Double or the margin truncates
                    val profit = saleAmount.amount - costAmount.amount
                    val marginPercent = profit * 100.0 / costAmount.amount
                    Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))
                    ProfitMarginSummary(
                        profit = profit,
                        marginPercent = marginPercent,
                        sellingBelowCost = saleAmount.amount <= costAmount.amount
                    )
                }

                // Reserve space so the last item isn't hidden behind the fixed SaveButton
                Spacer(modifier = Modifier.height(SaveButtonReserve))
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
                    remoteImageUrl = remoteImageUrl,
                    catalogProductId = catalogProductId
                )
            },
            // Stays above the navigation bar and the keyboard while typing
            // in the form fields above
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .safeBottomImePadding()
        )

        // Snackbar for save errors
        SnackyHost(hostState = snackyHostState)

        // Discard-changes confirmation
        ConfirmyHost(hostState = confirmyHostState)

        // Live transcript while a session runs — closes itself when the
        // state leaves Listening; cancel (button, back, or outside tap)
        // aborts the session
        if (voiceState is VoiceState.Listening) {
            VoiceListeningDialog(
                partialText = voicePartial,
                onCancel = voiceInput::cancel
            )
        }
    }
}

// Shared form-field chrome — every input on this screen (and the price
// bottom sheet's PriceField) draws from one place, so shape, label
// typography, value typography, and border colors can never drift apart
@Composable
private fun FieldLabel(labelRes: Int) {
    Text(
        text = stringResource(labelRes),
        fontFamily = BKoodak,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun fieldShape() = RoundedCornerShape(dimen(R.dimen.radius_md))

@Composable
private fun fieldTextStyle() = TextStyle(
    fontFamily = BKoodak,
    fontWeight = FontWeight.Bold,
    fontSize = dimenTextSize(R.dimen.text_size_md)
)

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline
)

@Composable
private fun ProductLoadingState(
    onNavigateBack: () -> Unit
) {
    // A skeleton that mirrors the form's own shape — reads as "the form is
    // coming" instead of an unrelated floating spinner
    Column(modifier = Modifier.fillMaxSize()) {
        AddProductTopBar(
            isEditMode = true,
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier.padding(horizontal = dimen(R.dimen.space_4)),
            verticalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_3))
        ) {
            Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))
            SkeletonBlock(height = 56.dp)   // name
            SkeletonBlock(height = 150.dp)  // scanner
            SkeletonBlock(height = 56.dp)   // stock + unit
            SkeletonBlock(height = 120.dp)  // image
        }
    }
}

@Composable
private fun SkeletonBlock(height: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(dimen(R.dimen.radius_md)))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    )
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

// Network errors mean the recognizer never reached the speech service —
// fa-IR recognition is online-only; speech timeouts and no-matches both
// mean "nothing heard"; permission errors explain themselves; everything
// else collapses into one generic retryable failure
private fun voiceErrorText(context: Context, code: Int): String = context.getString(
    when (code) {
        SpeechRecognizer.ERROR_NETWORK,
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> R.string.error_voice_network
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
        SpeechRecognizer.ERROR_NO_MATCH -> R.string.error_voice_no_match
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> R.string.error_voice_permission_denied
        else -> R.string.error_voice_failed
    }
)

// Voice feedback reuses the form's own labels so the summary reads
// exactly like the form does
private fun VoiceField.labelRes(): Int = when (this) {
    VoiceField.NAME -> R.string.product_name
    VoiceField.INITIAL_STOCK -> R.string.initial_stock
    VoiceField.COST_PRICE -> R.string.cost_price
    VoiceField.SALE_PRICE -> R.string.sale_price
}

@Composable
fun AddProductTopBar(
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit,
    onMicClick: (() -> Unit)? = null,
    isListening: Boolean = false
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

        // The actions group as one child so SpaceBetween keeps the title
        // left and both icons right together
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2))
        ) {
            // Persian voice input — only drawn when a speech service exists
            // (onMicClick != null); highlighted while a session is running
            if (onMicClick != null) {
                IconButton(
                    onClick = onMicClick,
                    modifier = Modifier
                        .padding(dimen(R.dimen.space_2))
                        .size(dimen(R.dimen.size_lg))
                        .clip(CircleShape)
                        .background(
                            if (isListening) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.mic_24px),
                        contentDescription = stringResource(R.string.voice_input),
                        tint = if (isListening) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Close icon
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(dimen(R.dimen.space_2))
                    .size(dimen(R.dimen.size_lg))
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
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
        label = { FieldLabel(R.string.product_name) },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(stringResource(R.string.error_product_name_required))
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = fieldShape(),
        colors = fieldColors(),
        singleLine = true,
        textStyle = fieldTextStyle()
    )
}


@Composable
private fun BarcodeField(
    value: String,
    onValueChange: (String) -> Unit,
    isLookingUp: Boolean = false,
    onNext: () -> Unit = {}
) {

    // Context for MediaPlayer
    val context = LocalContext.current

    // The scanner card and the field form one control: scan, and the result
    // lands in the field directly beneath it
    Column(verticalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_2))) {
        CompactBarcodeScanner(
            onBarcodeDetected = { detectedBarcode ->

                // Play barcode success sound
                BarcodeSoundPlayer.playBarcodeSuccessSound(context)

                onValueChange(detectedBarcode)

            },
            startPaused = true,
            modifier = Modifier.fillMaxWidth(),
            cardRadius = dimen(R.dimen.radius_md)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { FieldLabel(R.string.barcode_optional) },
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
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { onNext() }),
            shape = fieldShape(),
            colors = fieldColors(),
            singleLine = true,
            textStyle = fieldTextStyle()
        )
    }
}

@Composable
private fun InitialStockField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { FieldLabel(R.string.initial_stock) },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        shape = fieldShape(),
        colors = fieldColors(),
        singleLine = true,
        textStyle = fieldTextStyle()
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
            label = { FieldLabel(R.string.unit_optional) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            shape = fieldShape(),
            colors = fieldColors(),
            singleLine = true,
            textStyle = fieldTextStyle()
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
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
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
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        visualTransformation = ThousandSeparatorTransformation(),
        shape = fieldShape(),
        colors = fieldColors(),
        singleLine = true,
        textStyle = fieldTextStyle()
    )
}

@Composable
private fun ProfitMarginSummary(
    profit: Long,
    marginPercent: Double,
    sellingBelowCost: Boolean
) {
    // One tinted summary block instead of loose floating texts — the
    // container color is derived from the semantic status token
    val statusTint = if (profit >= 0) {
        MaterialTheme.colorScheme.success
    } else {
        MaterialTheme.colorScheme.customError
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimen(R.dimen.radius_md)))
            .background(statusTint.copy(alpha = 0.10f))
            .padding(
                horizontal = dimen(R.dimen.space_3),
                vertical = dimen(R.dimen.space_2)
            ),
        verticalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_1))
    ) {
        Text(
            text = stringResource(
                R.string.profit_margin_hint,
                PriceValidator.formatPrice(profit),
                String.format("%.1f", marginPercent)
            ),
            fontFamily = BKoodak,
            fontWeight = FontWeight.Bold,
            fontSize = dimenTextSize(R.dimen.text_size_sm),
            color = statusTint
        )
        if (sellingBelowCost) {
            Text(
                text = stringResource(R.string.warning_selling_below_cost),
                fontFamily = BKoodak,
                fontSize = dimenTextSize(R.dimen.text_size_sm),
                color = MaterialTheme.colorScheme.customError
            )
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
        shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
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
