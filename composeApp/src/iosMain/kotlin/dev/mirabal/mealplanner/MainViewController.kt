package dev.mirabal.mealplanner

import androidx.compose.ui.window.ComposeUIViewController
import app.cash.sqldelight.driver.native.NativeSqliteDriver

fun MainViewController() = ComposeUIViewController {
    val driver = NativeSqliteDriver(MealPlannerDatabase.Schema, "MealPlanner.db")
    App(AppDependencies(driver))
}
