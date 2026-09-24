package ir.yar.anbar.ui.screens.setting

// imports
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MenuAnchorType
import ir.yar.anbar.R
import ir.yar.anbar.domain.model.UnitOfMeasure
import ir.yar.anbar.ui.theme.AppFont.BKoodak
import ir.yar.anbar.ui.theme.Beirut_Medium
import ir.yar.anbar.utils.dimen
import ir.yar.anbar.utils.dimenTextSize
import ir.yar.anbar.utils.str


/**
 * Settings card for choosing the unit pre-selected on the add-product form.
 * State is hoisted: [selected] comes from the persisted preference and
 * [onSelect] writes it back. A default unit always exists (PIECE out of the
 * box), so — unlike the product form's picker — there is no "none" option.
 * The dropdown offers only [visibleUnits] (plus the current selection, so it
 * can always be changed away from).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitSelector(
    selected: UnitOfMeasure?,
    onSelect: (UnitOfMeasure) -> Unit,
    visibleUnits: Set<String> = UnitOfMeasure.values().map { it.name }.toSet()
) {
    var expanded by remember { mutableStateOf(false) }

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
                text = str(R.string.default_unit),
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_lg),
                color = MaterialTheme.colorScheme.onSurface
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                // faName is the display label; the enum name is what gets saved
                OutlinedTextField(
                    value = selected?.faName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            str(R.string.units),
                            fontFamily = BKoodak,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(dimen(R.dimen.radius_md)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = BKoodak,
                        fontWeight = FontWeight.Bold,
                        fontSize = dimenTextSize(R.dimen.text_size_md)
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
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
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewUnitSelector() {
    var tempUnit by remember { mutableStateOf(UnitOfMeasure.PIECE) }

    MaterialTheme {
        UnitSelector(
            selected = tempUnit,
            onSelect = { tempUnit = it }
        )
    }
}
