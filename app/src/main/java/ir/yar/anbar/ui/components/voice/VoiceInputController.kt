package ir.yar.anbar.ui.components.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Recognition session state. [Result.candidates] is ordered best-first —
 * later steps can offer the alternates as quick corrections for mishears.
 */
sealed interface VoiceState {
    data object Idle : VoiceState
    data object Listening : VoiceState
    data class Result(val candidates: List<String>) : VoiceState
    data class Error(val code: Int) : VoiceState
}

/**
 * Persian (fa-IR) speech-to-text wrapper around the platform
 * [SpeechRecognizer]. A fresh recognizer instance is created per session —
 * instances left in an error state refuse to restart, and per-session
 * creation sidesteps ERROR_RECOGNIZER_BUSY entirely.
 *
 * All methods must be called from the main thread (composition and click
 * handlers already are); callbacks arrive there too.
 */
class VoiceInputController(
    context: Context,
) {
    val state: StateFlow<VoiceState> = MutableStateFlow<VoiceState>(VoiceState.Idle)
    private val stateFlow = state as MutableStateFlow<VoiceState>

    private val appContext = context.applicationContext
    private var recognizer: SpeechRecognizer? = null

    /** True when the device has a speech recognition service at all. */
    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(appContext)

    /** Starts a new recognition session, replacing any previous one. */
    fun start() {
        destroy()
        stateFlow.value = VoiceState.Listening
        recognizer = SpeechRecognizer.createSpeechRecognizer(appContext).apply {
            setRecognitionListener(listener)
            startListening(recognitionIntent)
        }
    }

    /**
     * Ends the session and asks the service for a final result — used when
     * the UI offers an explicit stop; leaving it out simply waits for the
     * user to pause speaking.
     */
    fun stop() {
        recognizer?.stopListening()
    }

    fun destroy() {
        recognizer?.destroy()
        recognizer = null
    }

    private val recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "fa-IR")
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        // Best-effort offline recognition — falls back to the network service
        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() = Unit

        override fun onError(error: Int) {
            stateFlow.value = VoiceState.Error(error)
        }

        override fun onResults(results: Bundle?) {
            val candidates = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                .orEmpty()
            // An empty result list is indistinguishable from a mishearing
            // from the caller's point of view — surface it as ERROR_NO_MATCH
            stateFlow.value =
                if (candidates.isEmpty()) VoiceState.Error(SpeechRecognizer.ERROR_NO_MATCH)
                else VoiceState.Result(candidates)
        }

        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }
}

/**
 * Screen-scoped controller with its recognizer released on leaving
 * composition — an undisposed SpeechRecognizer leaks its service connection.
 */
@Composable
fun rememberVoiceInputController(): VoiceInputController {
    val context = LocalContext.current
    val controller = remember { VoiceInputController(context) }
    DisposableEffect(controller) {
        onDispose { controller.destroy() }
    }
    return controller
}
