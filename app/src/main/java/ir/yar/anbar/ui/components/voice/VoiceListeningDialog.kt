package ir.yar.anbar.ui.components.voice

import ir.yar.anbar.ui.theme.Vazir
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ir.yar.anbar.R
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize

/**
 * Live-transcript dialog for a running recognition session. The interim
 * transcript streams in while the user speaks; the dialog closes by itself
 * when the session ends (result or error states replace Listening), so the
 * only manual exit is cancel, which aborts the session.
 *
 * Motion: spring entrance, pulsing rings around the mic, animated sound
 * bars, and a cross-fade between the hint and the live transcript.
 */
@Composable
fun VoiceListeningDialog(
    partialText: String,
    onCancel: () -> Unit,
) {
    // Spring entrance — scale up + fade in
    val enter = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        enter.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = dimen(R.dimen.space_6))
                .fillMaxWidth()
                .graphicsLayer {
                    val s = 0.85f + 0.15f * enter.value
                    scaleX = s
                    scaleY = s
                    alpha = enter.value.coerceIn(0f, 1f)
                },
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = dimen(R.dimen.space_6),
                    vertical = dimen(R.dimen.space_6)
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.voice_listening_title),
                    fontFamily = Vazir,
                    fontSize = dimenTextSize(R.dimen.text_size_lg),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

                PulsingMic()

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_2)))

                SoundBars()

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_4)))

                // Transcript card — grows with the text, cross-fades between
                // the hint and the live words
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 72.dp)
                        .clip(RoundedCornerShape(dimen(R.dimen.radius_md)))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                        .animateContentSize()
                        .padding(dimen(R.dimen.space_3)),
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(
                        targetState = partialText.isBlank(),
                        animationSpec = tween(250),
                        label = "transcript"
                    ) { isEmpty ->
                        if (isEmpty) {
                            Text(
                                text = stringResource(R.string.voice_listening_hint),
                                fontFamily = Vazir,
                                fontSize = dimenTextSize(R.dimen.text_size_md),
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = partialText,
                                fontFamily = Vazir,
                                fontWeight = FontWeight.Bold,
                                fontSize = dimenTextSize(R.dimen.text_size_lg),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimen(R.dimen.space_5)))

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(dimen(R.dimen.radius_md))
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontFamily = Vazir,
                        modifier = Modifier.padding(vertical = dimen(R.dimen.space_1))
                    )
                }
            }
        }
    }
}

/** Mic button with two staggered rings expanding and fading outward. */
@Composable
private fun PulsingMic() {
    val transition = rememberInfiniteTransition(label = "mic_pulse")

    val ring1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring1"
    )
    val ring2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(900)
        ),
        label = "ring2"
    )
    // Gentle breathing on the mic itself
    val breathe by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        listOf(ring1, ring2).forEach { progress ->
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .graphicsLayer {
                        val s = 1f + progress * 0.8f
                        scaleX = s
                        scaleY = s
                        alpha = (1f - progress) * 0.35f
                    }
                    .background(primary, CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .size(84.dp)
                .graphicsLayer {
                    scaleX = breathe
                    scaleY = breathe
                }
                .background(primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.mic_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

/** Five bars bouncing at different speeds — a lightweight "audio" feel. */
@Composable
private fun SoundBars() {
    val transition = rememberInfiniteTransition(label = "bars")
    val durations = listOf(520, 680, 450, 640, 560)

    Row(
        modifier = Modifier.height(32.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        durations.forEachIndexed { index, duration ->
            val fraction by transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 90)
                ),
                label = "bar$index"
            )
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(32.dp * fraction)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            )
        }
    }
}