package dev.mirabal.mealplanner.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mirabal.mealplanner.shoppinglist.model.ItemName
import dev.mirabal.mealplanner.shoppinglist.model.Quantity
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val repository: ItemRepository,
) : ViewModel() {

    val items: StateFlow<List<ShoppingListUiItem>> = repository.getAll()
        .map { items -> items.map { it.toUiItem() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _quantityInput = MutableStateFlow("")
    val quantityInput: StateFlow<String> = _quantityInput.asStateFlow()

    val canAdd: StateFlow<Boolean> = combine(_inputText, _quantityInput) { name, qty ->
        name.isNotBlank() && (qty.isBlank() || Quantity.parse(qty) != null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    fun onQuantityChanged(text: String) {
        _quantityInput.value = text
    }

    fun onAddClicked() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return
        val rawQuantity = _quantityInput.value
        val quantity = if (rawQuantity.isBlank()) null else Quantity.parse(rawQuantity) ?: return
        viewModelScope.launch {
            repository.add(ItemName(text), quantity)
            _inputText.value = ""
            _quantityInput.value = ""
        }
    }
}

private fun ShoppingItem.toUiItem() = ShoppingListUiItem(
    id = id.value.toString(),
    label = when (val q = quantity) {
        is Quantity.WholeNumber -> "${q.value} ${name.value}"
        null -> name.value
    },
)
