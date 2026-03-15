package dev.mirabal.mealplanner.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel internal constructor(private val repository: ItemRepository) : ViewModel() {
    val items: StateFlow<List<ShoppingItem>> = repository.observeItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(name: String) {
        viewModelScope.launch { repository.addItem(ItemName(name)) }
    }
}
