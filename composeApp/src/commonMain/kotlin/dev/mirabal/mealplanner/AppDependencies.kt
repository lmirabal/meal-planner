package dev.mirabal.mealplanner

import dev.mirabal.mealplanner.shoppinglist.ItemRepository
import dev.mirabal.mealplanner.shoppinglist.InMemoryItemRepository

class AppDependencies {
    val itemRepository: ItemRepository = InMemoryItemRepository()
}
