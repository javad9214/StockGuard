package ir.yar.anbar.ui.components.voice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import ir.yar.anbar.R
import ir.yar.anbar.ui.theme.BKoodak
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize

/**
 * Live-transcript dialog for a running recognition session. The interim
 * transcript streams in while the user speaks; the dialog closes by itself
 * when the session ends (result or error states replace Listening), so the
 * only manual exit is cancel, which aborts the session.
 */
@Composable
fun VoiceListeningDialog(
    partialText: String,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = stringResource(R.string.voice_listening_title),
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_lg)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.mic_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimen(R.dimen.size_xl))
                )
                Spacer(modifier = Modifier.height(dimen(R.dimen.space_3)))
                if (partialText.isBlank()) {
                    // Before the first word lands, show what to say instead
                    // of an empty box
                    Text(
                        text = stringResource(R.string.voice_listening_hint),
                        fontFamily = BKoodak,
                        fontSize = dimenTextSize(R.dimen.text_size_md),
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    Text(
                        text = partialText,
                        fontFamily = BKoodak,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimenTextSize(R.dimen.text_size_lg),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.cancel))
            }
        },
        shape = RoundedCornerShape(dimen(R.dimen.radius_md))
    )
}
