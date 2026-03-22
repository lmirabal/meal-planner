plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

configurations.all {
    exclude(group = "androidx.lifecycle")
    exclude(group = "androidx.savedstate")
    exclude(group = "org.jetbrains.compose.annotation-internal")
    exclude(group = "org.jetbrains.compose.collection-internal")
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.compose.runtime") {
            useTarget("androidx.compose.runtime:${requested.name}:${libs.versions.composeMultiplatform.get()}")
        }
    }
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
            binaryOption("bundleId", "dev.mirabal.mealplanner")
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
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

val verifyXcodeBuild by tasks.registering(Exec::class) {
    group = "verification"
    commandLine(
        "xcodebuild",
        "-project", "../iosApp/iosApp.xcodeproj",
        "-scheme", "iosApp",
        "-destination", "generic/platform=iOS Simulator",
        "build"
    )
}

tasks.named("check") {
    dependsOn(verifyXcodeBuild)
}
