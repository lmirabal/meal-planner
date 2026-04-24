package dev.mirabal.mealplanner.shoppinglist.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
}
