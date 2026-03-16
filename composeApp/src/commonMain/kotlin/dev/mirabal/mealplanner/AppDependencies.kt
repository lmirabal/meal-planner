package dev.mirabal.mealplanner

import app.cash.sqldelight.db.SqlDriver
import dev.mirabal.mealplanner.shopping.ShoppingListViewModel
import dev.mirabal.mealplanner.shopping.SqlDelightItemRepository

class AppDependencies(driver: SqlDriver) {
    private val database = MealPlannerDatabase(driver)
    private val itemRepository = SqlDelightItemRepository(database.mealPlannerDatabaseQueries)
    val shoppingListViewModel = ShoppingListViewModel(itemRepository)
}
