package com.example.composetrainer.ui.screens.productlist.serverlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.composetrainer.R
import com.example.composetrainer.ui.theme.BMitra
import com.example.composetrainer.ui.theme.Beirut_Medium
import com.example.composetrainer.ui.viewmodels.SortOrder
import com.example.composetrainer.utils.dimen
import com.example.composetrainer.utils.dimenTextSize
import com.example.composetrainer.utils.str

@Composable
fun ServerProductListScreen(
    sortOrder: SortOrder,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSortOrderSelected: (SortOrder) -> Unit,
    onScanBarcode: () -> Unit,
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = dimen(R.dimen.space_6), end = dimen(R.dimen.space_2)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                str(R.string.products),
                fontFamily = Beirut_Medium,
                fontSize = dimenTextSize(R.dimen.text_size_xl)
            )

            IconButton(onClick = { onSortOrderSelected(sortOrder) }) {
                Icon(
                    Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Sort"
                )
            }


        }

        // SearchBar with barcode scan button
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            trailingIcon = {
                Row {
                    // Barcode scanner button
                    IconButton(onClick = onScanBarcode) {
                        Icon(
                            painter = painterResource(id = R.drawable.barcode_scanner_24px),
                            contentDescription = "Scan Barcode"
                        )
                    }

                    // Clear button
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimen(R.dimen.space_4),
                    vertical = dimen(R.dimen.space_2)
                ),
            placeholder = {
                Text(
                    str(R.string.search_products),
                    fontFamily = BMitra
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )
    }
}