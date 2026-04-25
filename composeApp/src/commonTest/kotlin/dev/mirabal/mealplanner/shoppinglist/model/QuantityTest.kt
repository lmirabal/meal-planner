package dev.mirabal.mealplanner.shoppinglist.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class QuantityTest {

    @Test
    fun wholeNumberRequiresPositiveValue() {
        assertFailsWith<IllegalArgumentException> { Quantity.WholeNumber(0) }
    }

    @Test
    fun wholeNumberRejectsNegativeValue() {
        assertFailsWith<IllegalArgumentException> { Quantity.WholeNumber(-1) }
    }

    @Test
    fun wholeNumberStoresValue() {
        assertEquals(3, Quantity.WholeNumber(3).value)
    }

    @Test
    fun wholeNumberSerializesToString() {
        assertEquals("3", Quantity.WholeNumber(3).serialize())
    }

    @Test
    fun parsePositiveIntReturnsWholeNumber() {
        assertEquals(Quantity.WholeNumber(3), Quantity.parse("3"))
    }

    @Test
    fun parseTrimsWhitespace() {
        assertEquals(Quantity.WholeNumber(3), Quantity.parse("  3  "))
    }

    @Test
    fun parseZeroReturnsNull() {
        assertNull(Quantity.parse("0"))
    }

    @Test
    fun parseNegativeReturnsNull() {
        assertNull(Quantity.parse("-1"))
    }

    @Test
    fun parseEmptyStringReturnsNull() {
        assertNull(Quantity.parse(""))
    }

    @Test
    fun parseBlankStringReturnsNull() {
        assertNull(Quantity.parse("   "))
    }

    @Test
    fun parseNonNumericReturnsNull() {
        assertNull(Quantity.parse("abc"))
    }
}
