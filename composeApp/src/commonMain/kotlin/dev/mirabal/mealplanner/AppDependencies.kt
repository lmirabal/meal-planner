package dev.mirabal.mealplanner

import dev.mirabal.mealplanner.db.ShoppingDatabase
import dev.mirabal.mealplanner.shoppinglist.ItemRepository
import dev.mirabal.mealplanner.shoppinglist.itemRepository

class AppDependencies {
    private val database = ShoppingDatabase(createSqlDriver())
    val itemRepository: ItemRepository = itemRepository(database)
}
