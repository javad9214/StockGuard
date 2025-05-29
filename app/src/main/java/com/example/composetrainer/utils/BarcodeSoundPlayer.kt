package com.example.composetrainer.utils

import android.content.Context
import android.media.ToneGenerator
import android.media.AudioManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Utility class for playing barcode scanner sounds
 */
object BarcodeSoundPlayer {

    /**
     * Plays the barcode scanner beep sound
     * @param context The Android context
     */
    fun playBarcodeSuccessSound(context: Context) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 300)

                // Wait for the tone to finish, then release
                delay(300)
                toneGenerator.release()
            }
        } catch (e: Exception) {
            // Handle exception
            e.printStackTrace()
        }
    }
}
