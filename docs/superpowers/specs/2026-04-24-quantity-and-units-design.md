# Iteration 1 — Quantity and Units: Design

## Context

Issue #2 adds quantity and unit support to shopping items (e.g. "300g mince beef", "⅓ tsp
cinnamon", "3 lemons"). The original spec was challenged and revised:

- The `REAL` DB column is replaced with TEXT serialisation for exact fraction representation
- `Unit` is renamed to `MeasurementUnit` to avoid a clash with Kotlin's built-in `Unit` type
- The quantity model uses a sealed hierarchy instead of float-with-epsilon-detection
- `piece` unit is dropped (not a common recipe unit)
- Mixed numbers (e.g. 2⅓) are scoped to a dedicated final slice

The feature is delivered as five vertical slices, each end-to-end and TDD'd with a commit per
green step.

---

## Slices

| # | Scope | New types |
|---|---|---|
| 1 | Whole number quantities, no unit | `WholeNumber` |
| 2 | Units | `MeasurementUnit`, `ItemAmount` |
| 3 | Fractions | `Fraction` |
| 4 | Decimals | `Decimal` |
| 5 | Mixed numbers (e.g. 2⅓) | Formatter extension only |

Each slice has its own implementation plan, written and executed sequentially.

---

## Domain Model

### `Quantity` sealed hierarchy

`composeApp/src/commonMain/kotlin/dev/mirabal/mealplanner/shoppinglist/model/Quantity.kt`

```kotlin
sealed interface Quantity {
    data class WholeNumber(val value: Int) : Quantity {
        init { require(value > 0) }
    }
    data class Fraction(val numerator: Int, val denominator: Int) : Quantity {
        init {
            require(numerator > 0)
            require(denominator > 0)
            require(gcd(numerator, denominator) == 1) // must be pre-normalised via Fraction.of()
        }
        companion object {
            fun of(numerator: Int, denominator: Int): Fraction {
                val g = gcd(numerator, denominator)
                return Fraction(numerator / g, denominator / g)
            }
        }
    }
    data class Decimal(val value: Double) : Quantity {
        init { require(value > 0) }
        // 2dp enforced at parse time (count chars after '.' in raw string, not via float arithmetic)
    }

    companion object {
        fun parse(input: String): Quantity?
    }
}
```

### Parser rules (applied in order)

1. Contains `/` → parse as `numerator/denominator`, normalise via `Fraction.of()` → `Fraction`
2. No decimal point, parses as positive `Int` → `WholeNumber`
3. Parses as `Double` with ≤ 2dp AND `value * 4` is a whole number → `Fraction` via `Fraction.of()`
   (e.g. `0.25→Fraction(1,4)`, `0.5→Fraction(1,2)`, `0.75→Fraction(3,4)`);
   slices 1–4 only convert values where `0 < value < 1`;
   slice 5 extends to values above 1 (e.g. `1.25→Fraction(5,4)`)
4. Parses as `Double` with ≤ 2dp → `Decimal`
5. Otherwise → `null`

2dp enforcement uses the raw input string (count characters after `.`), not float arithmetic.

### `MeasurementUnit`

`composeApp/src/commonMain/kotlin/dev/mirabal/mealplanner/shoppinglist/model/MeasurementUnit.kt`

```kotlin
value class MeasurementUnit(val value: String) {
    init {
        require(value == value.trim())
        require(value.isNotEmpty())
    }
}
```

Predefined common units: `g`, `kg`, `ml`, `l`, `tsp`, `tbsp`, `cm`. Free text also accepted.

### `ItemAmount`

`composeApp/src/commonMain/kotlin/dev/mirabal/mealplanner/shoppinglist/model/ItemAmount.kt`

```kotlin
data class ItemAmount(val quantity: Quantity, val unit: MeasurementUnit?)
```

A unit without a quantity is not representable. A quantity without a unit is valid (e.g. "3 lemons").

### `ShoppingItem` change

`val amount: ItemAmount?` added — `null` for name-only items (e.g. "bread").

---

## Persistence

### Schema (version 2)

`composeApp/src/commonMain/sqldelight/dev/mirabal/mealplanner/db/ShoppingDatabase.sq`

```sql
CREATE TABLE ShoppingItem (
    id TEXT NOT NULL PRIMARY KEY,
    list_id TEXT NOT NULL,
    name TEXT NOT NULL,
    quantity TEXT,
    unit TEXT,
    checked INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```

Both `quantity` and `unit` are nullable. An item without quantity stores `NULL` in both.

### Migration file

`composeApp/src/commonMain/sqldelight/dev/mirabal/mealplanner/db/2.sqm`

Destructive migration (dev mode — data is cleared, no row migration needed):

```sql
DROP TABLE ShoppingItem;
CREATE TABLE ShoppingItem (
    id TEXT NOT NULL PRIMARY KEY,
    list_id TEXT NOT NULL,
    name TEXT NOT NULL,
    quantity TEXT,
    unit TEXT,
    checked INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```

### Updated queries

```sql
insertItem:
INSERT INTO ShoppingItem (id, list_id, name, quantity, unit, checked, created_at, updated_at)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

selectAllItems:
SELECT id, list_id, name, quantity, unit, checked, created_at, updated_at
FROM ShoppingItem ORDER BY created_at DESC, rowid DESC;
```

### TEXT serialisation

| Kotlin type | DB value |
|---|---|
| `WholeNumber(3)` | `"3"` |
| `Fraction(1,3)` | `"1/3"` |
| `Decimal(1.5)` | `"1.5"` |
| `null` | `NULL` |

`Quantity.parse()` is reused for deserialisation — no separate DB-mapping logic.

### Repository signature

```kotlin
interface ItemRepository {
    fun getAll(): Flow<List<ShoppingItem>>
    suspend fun add(name: ItemName, amount: ItemAmount?)
}
```

---

## Presentation

### `QuantityFormatter`

`composeApp/src/commonMain/kotlin/dev/mirabal/mealplanner/shoppinglist/QuantityFormatter.kt`

```kotlin
fun formatItem(name: ItemName, amount: ItemAmount?): String
```

| Amount | Output |
|---|---|
| `null` | `"bread"` |
| `WholeNumber(3), unit = null` | `"3 lemons"` |
| `WholeNumber(300), MeasurementUnit("g")` | `"300g mince beef"` |
| `Fraction(1,3), MeasurementUnit("tsp")` | `"⅓ tsp cinnamon"` |
| `Decimal(1.5), MeasurementUnit("kg")` | `"1.5kg chicken"` |

Vulgar fraction map (slice 3): `1/4→¼`, `1/2→½`, `3/4→¾`, `1/3→⅓`, `2/3→⅔`.
Unknown fractions (e.g. `Fraction(5,7)`) fall back to `"numerator/denominator"` (e.g. `"5/7"`)
until slice 5.

Mixed number display (`Fraction(7,3)→2⅓`) is slice 5 only.

### ViewModel additions

`composeApp/src/commonMain/kotlin/dev/mirabal/mealplanner/shoppinglist/ShoppingListViewModel.kt`

```kotlin
val quantityInput: StateFlow<String>
val unitInput: StateFlow<String>
fun onQuantityChanged(text: String)
fun onUnitChanged(text: String)
```

`onAddClicked()` parses `quantityInput` via `Quantity.parse()` and builds `ItemAmount?`.

Add button disabled if name is blank, or if quantity input is non-empty but `Quantity.parse()` returns `null`.

### Screen changes

`composeApp/src/commonMain/kotlin/dev/mirabal/mealplanner/shoppinglist/ShoppingListScreen.kt`

Input row layout:
```
[ Name field (flex) ] [ Qty ] [ Unit ▼ ] [ Add ]
```

- **Qty**: `OutlinedTextField`, keyboard allows digits, `.`, `/`
- **Unit**: `ExposedDropdownMenuBox` — shows predefined units, also editable for free text
- All fields clear after successful add
- List items display `formatItem(item.name, item.amount)` instead of just the name

---

## Files to create

- `shoppinglist/model/Quantity.kt`
- `shoppinglist/model/MeasurementUnit.kt`
- `shoppinglist/model/ItemAmount.kt`
- `shoppinglist/QuantityFormatter.kt`
- `db/2.sqm`

## Files to modify

- `shoppinglist/model/ShoppingItem.kt` — add `amount: ItemAmount?`
- `ShoppingDatabase.sq` — updated schema + queries
- `shoppinglist/ItemRepository.kt` — updated `add` signature
- `shoppinglist/SqlDelightItemRepository.kt` — serialise/deserialise `ItemAmount`
- `shoppinglist/ShoppingListViewModel.kt` — quantity + unit input state
- `shoppinglist/ShoppingListScreen.kt` — new input fields + updated item display
