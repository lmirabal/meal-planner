package dev.mirabal.mealplanner

import dev.mirabal.mealplanner.shoppinglist.ItemRepository
import dev.mirabal.mealplanner.shoppinglist.itemRepository

class AppDependencies {
    val itemRepository: ItemRepository = itemRepository()
}
