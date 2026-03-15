package dev.mirabal.mealplanner

import dev.mirabal.mealplanner.shopping.InMemoryItemRepository
import dev.mirabal.mealplanner.shopping.ShoppingListViewModel

class AppDependencies {
    private val itemRepository = InMemoryItemRepository()
    val shoppingListViewModel = ShoppingListViewModel(itemRepository)
}
