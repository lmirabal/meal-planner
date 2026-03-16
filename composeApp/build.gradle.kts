@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.cupertino)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.compose.ui.test)
        }
    }
}

val buildIosSimulator = tasks.register<Exec>("buildIosSimulator") {
    group = "verification"
    description = "Build the Xcode app for the iOS simulator to catch linker errors"
    commandLine(
        "xcodebuild",
        "-project", "${rootDir}/iosApp/iosApp.xcodeproj",
        "-scheme", "iosApp",
        "-destination", "generic/platform=iOS Simulator",
        "build",
        "CODE_SIGNING_ALLOWED=NO",
    )
}

tasks.named("check") {
    dependsOn(buildIosSimulator)
}

sqldelight {
    databases {
        create("MealPlannerDatabase") {
            packageName.set("dev.mirabal.mealplanner")
        }
    }
}
