package dev.mirabal.mealplanner.shoppinglist.model

sealed interface Quantity {
    fun serialize(): String

    data class WholeNumber(val value: Int) : Quantity {
        init { require(value > 0) }
        override fun serialize(): String = value.toString()
    }

    companion object {
        fun parse(input: String): Quantity? {
            val trimmed = input.trim()
            if (trimmed.isEmpty()) return null
            return trimmed.toIntOrNull()?.takeIf { it > 0 }?.let { WholeNumber(it) }
        }
    }
}
