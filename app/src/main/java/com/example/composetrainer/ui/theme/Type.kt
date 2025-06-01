package com.example.composetrainer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.composetrainer.R

// Define font families
val BKoodak = FontFamily(
    Font(R.font.b_koodak_bd, FontWeight.Bold),
    Font(R.font.b_koodak_o, FontWeight.Normal)
)

val BNazanin = FontFamily(
    Font(R.font.bnazanin, FontWeight.Normal)
)

val Zar = FontFamily(
    Font(R.font.zar, FontWeight.Normal),
    Font(R.font.zar_bold, FontWeight.Bold)
)

val BComps = FontFamily(
    Font(R.font.b_comps_bd, FontWeight.Bold)
)

val BLotus = FontFamily(
    Font(R.font.b_lotus_bd, FontWeight.Bold)
)

val BMitra = FontFamily(
    Font(R.font.b_mitra_bd, FontWeight.Bold)
)

val BHoma = FontFamily(
    Font(R.font.b_homa, FontWeight.Bold)
)

val BRoya = FontFamily(
    Font(R.font.b_roya_bd, FontWeight.Bold)
)

val Kamran = FontFamily(
    Font(R.font.kamran_b, FontWeight.Bold)
)

val Beirut_Medium = FontFamily(
    Font(R.font.beirut_md_mrt, FontWeight.Medium)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Zar,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = BKoodak,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = BNazanin,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = BComps,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Zar,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Zar,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    titleMedium = TextStyle(
        fontFamily = BKoodak,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = BKoodak,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)