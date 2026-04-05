package dev.mirabal.mealplanner.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mirabal.mealplanner.shoppinglist.model.ItemName
import dev.mirabal.mealplanner.shoppinglist.model.ShoppingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val repository: ItemRepository,
) : ViewModel() {

    val items: StateFlow<List<ShoppingItem>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    fun onAddClicked() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            repository.add(ItemName(text))
            _inputText.value = ""
        }
    }
}
