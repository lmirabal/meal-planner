package dev.mirabal.mealplanner.shopping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import io.github.robinpcrd.cupertino.CupertinoBorderedTextField
import io.github.robinpcrd.cupertino.CupertinoButton
import io.github.robinpcrd.cupertino.ExperimentalCupertinoApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun ShoppingListScreen(viewModel: ShoppingListViewModel) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.windowInsetsPadding(WindowInsets.safeContent)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CupertinoBorderedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Item name") },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
            )
            CupertinoButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.addItem(inputText)
                        inputText = ""
                    }
                }
            ) {
                Text("Add")
            }
        }
        LazyColumn {
            items(items, key = { it.id.value }) { item ->
                Text(
                    text = item.name.value,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    }
}
