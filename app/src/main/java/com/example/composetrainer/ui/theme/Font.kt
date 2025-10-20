package com.example.composetrainer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/**
 * Font utility class to easily access font families throughout the app
 */
object AppFont {
    // Main font families
    val Zar: FontFamily = com.example.composetrainer.ui.theme.Zar
    val BKoodak: FontFamily = com.example.composetrainer.ui.theme.BKoodak
    val BNazanin: FontFamily = com.example.composetrainer.ui.theme.BNazanin
    val BComps: FontFamily = com.example.composetrainer.ui.theme.BComps
    val MRTPoster: FontFamily = com.example.composetrainer.ui.theme.MRTPoster

    // Default app font
    val Default: FontFamily = BComps

    // Semantic fonts
    val Header: FontFamily = BKoodak
    val Body: FontFamily = Zar
    val Caption: FontFamily = BNazanin
    val Display: FontFamily = BComps
}

/**
 * Extension function to get bold version of a font
 */
@Composable
fun FontFamily.bold() = this to FontWeight.Bold

/**
 * Extension function to get normal weight version of a font
 */
@Composable
fun FontFamily.normal() = this to FontWeight.Normal