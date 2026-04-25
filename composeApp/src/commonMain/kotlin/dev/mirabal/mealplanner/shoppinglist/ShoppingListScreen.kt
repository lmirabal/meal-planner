package dev.mirabal.mealplanner.shoppinglist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mirabal.mealplanner.shoppinglist.model.Quantity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShoppingListScreen(viewModel: ShoppingListViewModel) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val quantityInput by viewModel.quantityInput.collectAsStateWithLifecycle()
    val canAdd by viewModel.canAdd.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Shopping List") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = viewModel::onInputChanged,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Item name") },
                    )
                    OutlinedTextField(
                        value = quantityInput,
                        onValueChange = viewModel::onQuantityChanged,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(80.dp),
                        placeholder = { Text("Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                    Button(
                        onClick = viewModel::onAddClicked,
                        enabled = canAdd,
                        modifier = Modifier.padding(start = 8.dp),
                    ) {
                        Text("Add")
                    }
                }
            }
            items(items, key = { it.id.value.toString() }) { item ->
                val label = when (val q = item.quantity) {
                    is Quantity.WholeNumber -> "${q.value} ${item.name.value}"
                    null -> item.name.value
                }
                ListItem(headlineContent = { Text(label) })
            }
        }
    }
}
