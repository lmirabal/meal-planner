package dev.mirabal.mealplanner

import androidx.compose.runtime.Composable
import dev.mirabal.mealplanner.shopping.ShoppingListScreen
import io.github.robinpcrd.cupertino.theme.CupertinoTheme

@Composable
fun App(dependencies: AppDependencies) {
    CupertinoTheme {
        ShoppingListScreen(dependencies.shoppingListViewModel)
    }
}
