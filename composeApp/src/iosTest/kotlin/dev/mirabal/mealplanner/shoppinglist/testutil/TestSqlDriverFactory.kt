package dev.mirabal.mealplanner.shoppinglist.testutil

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import kotlin.uuid.Uuid

internal actual fun createTestSqlDriver(schema: SqlSchema<QueryResult.Value<Unit>>): SqlDriver =
    NativeSqliteDriver(
        DatabaseConfiguration(
            name = "test-${Uuid.random()}.db",
            version = schema.version.toInt(),
            create = { connection -> wrapConnection(connection) { schema.create(it) } },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) { schema.migrate(it, oldVersion.toLong(), newVersion.toLong()) }
            },
            inMemory = true,
        )
    )
