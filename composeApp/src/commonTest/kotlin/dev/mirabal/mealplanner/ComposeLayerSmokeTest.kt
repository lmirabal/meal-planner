package dev.mirabal.mealplanner

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

class ComposeLayerSmokeTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun composeLayerInitialises() = runComposeUiTest {
        setContent {
            MaterialTheme {}
        }
    }
}
