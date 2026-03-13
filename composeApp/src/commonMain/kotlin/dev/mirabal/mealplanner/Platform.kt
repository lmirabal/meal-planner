package dev.mirabal.mealplanner

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform