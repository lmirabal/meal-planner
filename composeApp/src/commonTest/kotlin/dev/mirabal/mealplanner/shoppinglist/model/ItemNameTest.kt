package dev.mirabal.mealplanner.shoppinglist.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ItemNameTest {

    @Test
    fun validNameConstructsSuccessfully() {
        assertEquals("Milk", ItemName("Milk").value)
    }

    @Test
    fun blankNameThrows() {
        assertFailsWith<IllegalArgumentException> { ItemName("") }
    }

    @Test
    fun whitespaceOnlyNameThrows() {
        assertFailsWith<IllegalArgumentException> { ItemName("  ") }
    }

    @Test
    fun leadingSpaceThrows() {
        assertFailsWith<IllegalArgumentException> { ItemName("  Milk") }
    }

    @Test
    fun trailingSpaceThrows() {
        assertFailsWith<IllegalArgumentException> { ItemName("Milk  ") }
    }

    @Test
    fun bothSidesSpaceThrows() {
        assertFailsWith<IllegalArgumentException> { ItemName("  Milk  ") }
    }
}
