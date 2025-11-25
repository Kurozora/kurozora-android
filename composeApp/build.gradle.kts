import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.aboutLibraries)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            implementation(libs.ktor.client.okhttp)
            implementation("com.google.android.material:material:1.9.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
            implementation(compose.material3AdaptiveNavigationSuite)

            implementation(libs.coroutines)
            implementation(libs.kotlinX.dateTime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeViewModel)
            implementation("media.kamel:kamel-image-default:1.0.8")
            implementation(libs.bundles.coil)
            implementation(libs.bundles.ktor)
            implementation(libs.bundles.multiplatformSettings)

            implementation(libs.navigation)
            implementation(libs.adaptive.core)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(files("../libs/kurozorakit-android-1.2.6.jar"))

            implementation(libs.mediamp.all)

            implementation(libs.aboutlibraries.core)
            implementation(libs.aboutlibraries.compose.m3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.coroutines.swing)
            implementation(libs.ktor.client.cio)
        }
    }
}

android {
    namespace = "app.kurozora"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "app.kurozora.android"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = "password"
            keyAlias = "kurozora"
            keyPassword = "password"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    implementation("androidx.core:core-splashscreen:1.0.1")
}

compose.desktop {
    application {
        mainClass = "app.kurozora.MainKt"

        nativeDistributions {
            includeAllModules = true
            targetFormats(TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "app.kurozora.android"
            packageVersion = "1.0.0"
        }

        buildTypes.release.proguard {
            isEnabled = false
        }
    }
}

compose.resources {
    publicResClass = true // ✅ Res.drawable.anime_banner gibi erişim sağlar
    //generateResClass = ResourcesExtension.ResourceClassGeneration.Always
    //srcDirs("build/flattenedResources")
}

aboutLibraries {
    export {
        // Define the output path for manual generation
        // Adjust the path based on your project structure (e.g., composeResources, Android res/raw)
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
    }
}
