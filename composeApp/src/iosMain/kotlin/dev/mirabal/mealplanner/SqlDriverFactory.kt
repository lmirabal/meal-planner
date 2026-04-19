package dev.mirabal.mealplanner

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.mirabal.mealplanner.db.ShoppingDatabase

internal actual fun createSqlDriver(): SqlDriver =
    NativeSqliteDriver(ShoppingDatabase.Schema, "ShoppingDatabase.db")
