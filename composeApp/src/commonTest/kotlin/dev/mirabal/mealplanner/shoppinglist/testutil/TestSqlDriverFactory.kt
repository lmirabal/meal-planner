package dev.mirabal.mealplanner.shoppinglist.testutil

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

internal expect fun createTestSqlDriver(schema: SqlSchema<QueryResult.Value<Unit>>): SqlDriver
