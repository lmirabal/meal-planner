package dev.mirabal.mealplanner

import app.cash.sqldelight.db.SqlDriver

internal expect fun createSqlDriver(): SqlDriver
