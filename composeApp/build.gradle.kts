import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
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
            //implementation("androidx.core:core-splashscreen:1.0.1")
            //implementation("media.kamel:kamel-image-android:1.0.8")
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
//            implementation(libs.bundles.kamel)
            //implementation("media.kamel:kamel-image:1.0.3")
            implementation("media.kamel:kamel-image-default:1.0.8")
            implementation(libs.bundles.coil)
            implementation(libs.bundles.ktor)
            implementation(libs.bundles.multiplatformSettings)

            implementation(libs.navigation)
            implementation(libs.adaptive.core)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            //                implementation(files("../libs/kurozorakit-android-1.0-SNAPSHOT.jar"))
            implementation(files("../libs/kurozorakit-android-1.2.6.jar"))

            implementation(libs.mediamp.all)
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
//// 🔧 Tüm alt klasörleri flatten eder ve tek dizine kopyalar
//        tasks.register<Copy>("flattenDrawables") {
//            val inputDir = file("src/commonMain/resources/drawable")
//            val outputDir = layout.buildDirectory.dir("flattenedResources/drawable")
//
//            from(inputDir) {
//                include("**/*.png", "**/*.jpg", "**/*.jpeg", "**/*.webp", "**/*.svg")
//                exclude("**/*.ico") // .ico desteklenmiyor
//
//                // Alt klasör isimlerini dosya adına ekle (örnek: icons/home.svg -> icons_home.svg)
//                eachFile { details ->
//                    val flattenedName = details.relativePath.pathString.replace("/", "_")
//                    details.relativePath = RelativePath(true, flattenedName)
//                }
//            }
//
//            into(outputDir)
//        }
//
//tasks.named("composeResources") {
//    dependsOn("flattenDrawables")
//}
