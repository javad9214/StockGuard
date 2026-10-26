package ir.yar.anbar.ui.components.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import ir.yar.anbar.domain.voice.VoiceFormCommandParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "VoiceInputController"

/**
 * How many times a session silently restarts after the service gives up on
 * silence (ERROR_NO_MATCH / ERROR_SPEECH_TIMEOUT) — each restart grants the
 * user another endpointer window before the error is finally surfaced.
 */
private const val MAX_AUTO_RESTARTS = 3

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
 * creation sidesteps ERROR_RECOGNIZER_BUSY entirely. Sessions also survive
 * the service's early silence verdicts: ERROR_NO_MATCH with an interim
 * transcript is delivered as a result, and bare silence errors silently
 * restart the session a few times before surfacing.
 *
 * All methods must be called from the main thread (composition and click
 * handlers already are); callbacks arrive there too.
 */
class VoiceInputController(
    context: Context,
) {
    val state: StateFlow<VoiceState> = MutableStateFlow<VoiceState>(VoiceState.Idle)
    private val stateFlow = state as MutableStateFlow<VoiceState>

    /** Live interim transcript of the running session ("" before speech). */
    val partialTranscript: StateFlow<String> = MutableStateFlow("")
    private val partialFlow = partialTranscript as MutableStateFlow<String>

    private val appContext = context.applicationContext
    private var recognizer: SpeechRecognizer? = null
    private var autoRestartsLeft = 0

    /** True when the device has a speech recognition service at all. */
    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(appContext)

    /** Starts a new recognition session, replacing any previous one. */
    fun start() {
        destroy()
        partialFlow.value = ""
        autoRestartsLeft = MAX_AUTO_RESTARTS
        stateFlow.value = VoiceState.Listening
        startRecognition()
    }

    private fun startRecognition() {
        recognizer = SpeechRecognizer.createSpeechRecognizer(appContext).apply {
            setRecognitionListener(listener)
            startListening(recognitionIntent)
        }
    }

    /**
     * Aborts the running session without a result — the exit path for
     * manually dismissing the listening dialog.
     */
    fun cancel() {
        destroy()
        partialFlow.value = ""
        stateFlow.value = VoiceState.Idle
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
        // Biases recognition toward the form's field labels — they are the
        // keywords VoiceFormCommandParser segments the transcript on. The
        // extra takes effect on API 33+; older services ignore it
        putExtra(
            RecognizerIntent.EXTRA_BIASING_STRINGS,
            ArrayList(VoiceFormCommandParser.biasingPhrases)
        )
        // Live interim transcripts while the user is still speaking —
        // consumed by the listening dialog
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        // No EXTRA_PREFER_OFFLINE: fa-IR has no downloadable offline model,
        // and preferring an undownloaded language fails the whole session
        // (ERROR_LANGUAGE_NOT_SUPPORTED / UNAVAILABLE) instead of falling
        // back to network recognition
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() = Unit

        override fun onError(error: Int) {
            // The UI collapses codes into user-facing messages; the exact
            // code (see SpeechRecognizer ERROR_* constants) only matters here
            Log.w(TAG, "Recognition failed, error code $error")
            // A destroyed recognizer can still race one last callback in —
            // once the session is no longer live (cancelled), ignore it
            if (stateFlow.value !is VoiceState.Listening) return
            val heardSpeech = partialFlow.value.isNotBlank()
            when {
                // The endpointer gave up but the interims prove speech was
                // heard — the partial is the best transcript this session
                // will produce, so deliver it instead of the error
                error == SpeechRecognizer.ERROR_NO_MATCH && heardSpeech ->
                    stateFlow.value = VoiceState.Result(listOf(partialFlow.value.trim()))
                // Silence verdicts arrive after just a couple of seconds of
                // pause — restart so the user gets more time to speak, as
                // long as the budget lasts
                (error == SpeechRecognizer.ERROR_NO_MATCH ||
                    error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) &&
                    autoRestartsLeft > 0 -> {
                    autoRestartsLeft--
                    destroy()
                    startRecognition()
                }
                else -> stateFlow.value = VoiceState.Error(error)
            }
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

        override fun onPartialResults(partialResults: Bundle?) {
            // Interim text; the authoritative result still arrives in
            // onResults once the user stops
            partialFlow.value = partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                .orEmpty()
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
