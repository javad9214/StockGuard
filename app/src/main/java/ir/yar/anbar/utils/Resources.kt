package ir.yar.anbar.utils

import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun str(@StringRes stringRes: Int): String = stringResource(id = stringRes)

@Composable
fun dimen(@DimenRes dimenRes: Int): Dp = dimensionResource(dimenRes)

@Composable
fun dimenTextSize(@DimenRes dimenRes: Int): TextUnit = dimensionResource(dimenRes).value.sp