package dev.mirabal.mealplanner

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

fun MainViewController() = ComposeUIViewController {
    var deps by remember { mutableStateOf<AppDependencies?>(null) }
    LaunchedEffect(Unit) {
        deps = withContext(Dispatchers.IO) { AppDependencies() }
    }
    deps?.let { App(it) }
}
