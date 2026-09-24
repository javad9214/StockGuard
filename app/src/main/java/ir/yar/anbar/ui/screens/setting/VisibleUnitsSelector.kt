package ir.yar.anbar.ui.screens.setting

// imports
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.UnitOfMeasure
import ir.yar.anbar.ui.theme.AppFont.BKoodak
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str

/**
 * Settings card controlling which units the unit pickers offer. The button
 * opens a bottom dialog with a checkbox per unit; confirming writes the new
 * set through [onChange]. State is hoisted — [visibleUnits] is the persisted
 * set of enum names.
 */
@Composable
fun VisibleUnitsSelector(
    visibleUnits: Set<String>,
    onChange: (Set<String>) -> Unit
) {
    var sheetVisible by remember { mutableStateOf(false) }
    // Draft copy the dialog edits; reset on every open so a cancelled session
    // never leaks its toggles into the next one
    var draft by remember { mutableStateOf(visibleUnits) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen(R.dimen.space_2), vertical = dimen(R.dimen.space_2)),
        shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen(R.dimen.space_6), vertical = dimen(R.dimen.space_4)),
            verticalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_3))
        ) {
            Text(
                text = str(R.string.visible_units),
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_lg),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = str(
                    R.string.visible_units_summary,
                    UnitOfMeasure.values().count { it.name in visibleUnits },
                    UnitOfMeasure.values().size
                ),
                fontSize = dimenTextSize(R.dimen.text_size_md),
                fontFamily = BKoodak,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = {
                    draft = visibleUnits
                    sheetVisible = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = str(R.string.choose_units),
                    fontFamily = Beirut_Medium,
                    fontSize = dimenTextSize(R.dimen.text_size_md)
                )
            }
        }
    }

    if (sheetVisible) {
        VisibleUnitsBottomDialog(
            selected = draft,
            onToggle = { unitName ->
                draft = if (unitName in draft) draft - unitName else draft + unitName
            },
            onConfirm = {
                onChange(draft)
                sheetVisible = false
            },
            onDismiss = { sheetVisible = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisibleUnitsBottomDialog(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(
            topStart = dimen(R.dimen.radius_lg),
            topEnd = dimen(R.dimen.radius_lg)
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = dimen(R.dimen.space_6),
                vertical = dimen(R.dimen.space_2)
            )
        ) {
            Text(
                text = str(R.string.visible_units),
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_lg),
                color = MaterialTheme.colorScheme.onSurface
            )

            // 29 rows won't fit any screen — the list must scroll on its own
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                items(UnitOfMeasure.values().toList()) { unit ->
                    val checked = unit.name in selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle(unit.name) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { onToggle(unit.name) },
                            modifier = Modifier.semantics {
                                contentDescription = unit.faName
                            }
                        )
                        Text(
                            text = unit.faName,
                            fontFamily = BKoodak,
                            fontSize = dimenTextSize(R.dimen.text_size_md),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimen(R.dimen.space_3)),
                horizontalArrangement = Arrangement.spacedBy(dimen(R.dimen.space_3))
            ) {
                // Confirm stays disabled while nothing is selected — hiding
                // every unit would leave the pickers empty
                Button(
                    onClick = onConfirm,
                    enabled = selected.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = str(R.string.confirm),
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_md)
                    )
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = str(R.string.cancel),
                        fontFamily = Beirut_Medium,
                        fontSize = dimenTextSize(R.dimen.text_size_md)
                    )
                }
            }
        }
    }
}
