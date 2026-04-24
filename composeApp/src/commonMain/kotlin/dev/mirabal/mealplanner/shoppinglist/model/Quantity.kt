package dev.mirabal.mealplanner.shoppinglist.model

sealed interface Quantity {
    fun serialize(): String

    data class WholeNumber(val value: Int) : Quantity {
        init { require(value > 0) }
        override fun serialize(): String = value.toString()
    }
}
