import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

tasks.withType<KotlinCompilationTask<*>>()
    .matching { it.name.endsWith("KotlinMetadata") }
    .configureEach { compilerOptions { allWarningsAsErrors = false } }

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
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
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.compose.uiTest)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

val frameworkDir = layout.buildDirectory.dir("bin/iosSimulatorArm64/debugFramework")

val verifyXcodeBuild by tasks.registering(Exec::class) {
    group = "verification"
    dependsOn(tasks.named("linkDebugFrameworkIosSimulatorArm64"))
    environment("OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED", "YES")
    commandLine(
        "xcodebuild",
        "-project", "${rootProject.projectDir}/iosApp/iosApp.xcodeproj",
        "-scheme", "iosApp",
        "-destination", "generic/platform=iOS Simulator",
        "FRAMEWORK_SEARCH_PATHS=${frameworkDir.get().asFile.absolutePath}",
        "build"
    )
}

tasks.named("check") {
    dependsOn(verifyXcodeBuild)
}
