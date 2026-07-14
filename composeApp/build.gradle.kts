import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.buildKonfig)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

val apiKey = localProperties.getProperty("KUROZORA_API_KEY") ?: ""

buildkonfig {
    packageName = "app.kurozora"

    defaultConfigs {
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "API_KEY",
            value = apiKey
        )
    }
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
            implementation("app.cash.sqldelight:android-driver:2.0.2")
            implementation("io.github.2307vivek:seeker:1.2.2")
            implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha13") // KMP destekli sürüm
            implementation("com.davemorrissey.labs:subsampling-scale-image-view-androidx:3.10.0")
            implementation("me.saket.telephoto:zoomable-image-coil3:0.14.0") // Sürüm numaranızı kontrol edin
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
            implementation("io.github.pdvrieze.xmlutil:serialization:0.90.3")

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

            implementation("app.kurozora:kurozorakit:1.3.0-SNAPSHOT")

            implementation(libs.mediamp.all)

            implementation(libs.aboutlibraries.core)
            implementation(libs.aboutlibraries.compose.m3)
            implementation(libs.bundles.filekit)
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
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
    }
}

// ─── App Icon Switcher ──────────────────────────────────────────────────────

data class AppIconEntry(
    val id: String,
    val relPath: String,
)

val allAppIcons = listOf(
    AppIconEntry("default", "Default/Kurozora/Kurozora.webp"),
    AppIconEntry("kuro-chan", "Default/Kuro-chan/kuro-chan.webp"),
    AppIconEntry("kurozora_day", "Default/Day/kurozora_day.webp"),
    AppIconEntry("kurozora_night", "Default/Night/kurozora_night.webp"),
    AppIconEntry("monokuro", "Anime/Monokuro/monokuro.webp"),
    AppIconEntry("6_colors", "Apple/6 Colors/6_colors.webp"),
    AppIconEntry("6_colors_inverted", "Apple/6 Colors Inverted/6_colors_inverted.webp"),
    AppIconEntry("ios_6", "Apple/iOS 6/iOS_6.webp"),
    AppIconEntry("ios_18", "Apple/iOS 18/iOS 18.webp"),
    AppIconEntry("kurozora_connect", "Apple/Kurozora Connect/kurozora_connect.webp"),
    AppIconEntry("kurozora_connect_dark", "Apple/Kurozora Connect/kurozora_connect~dark.webp"),
    AppIconEntry("kurozora_support", "Apple/Kurozora Support/kurozora_support.webp"),
    AppIconEntry("kurozora_support_inverted", "Apple/Kurozora Support Inverted/kurozora_support_inverted.webp"),
    AppIconEntry("kurozora_support_inverted_dark", "Apple/Kurozora Support Inverted/kurozora_support_inverted~dark.webp"),
    AppIconEntry("cookiezora", "Desserts/Cookiezora/cookiezora.webp"),
    AppIconEntry("kuro_caramel", "Desserts/Kuro Caramel/kuro_caramel.webp"),
    AppIconEntry("kurolicious", "Desserts/Kurolicious/kurolicious.webp"),
    AppIconEntry("eggatha", "Event/Eggatha/eggatha.webp"),
    AppIconEntry("eggstein", "Event/Eggstein/eggstein.webp"),
    AppIconEntry("hanabi", "Event/Hanabi/hanabi.webp"),
    AppIconEntry("john", "Event/John/john.webp"),
    AppIconEntry("john_dark", "Event/John/john~dark.webp"),
    AppIconEntry("love_bug", "Event/Love Bug/love_bug.webp"),
    AppIconEntry("melting_kiss", "Event/Melting Kiss/melting_kiss.webp"),
    AppIconEntry("sweet_return", "Event/Sweet Return/sweet_return.webp"),
    AppIconEntry("white_of_crime", "Event/White of Crime/white_of_crime.webp"),
    AppIconEntry("amber", "Gems/Amber/amber.webp"),
    AppIconEntry("amber_dark", "Gems/Amber/amber~dark.webp"),
    AppIconEntry("amethyst", "Gems/Amethyst/amethyst.webp"),
    AppIconEntry("amethyst_dark", "Gems/Amethyst/amethyst~dark.webp"),
    AppIconEntry("emerald", "Gems/Emerald/emerald.webp"),
    AppIconEntry("emerald_dark", "Gems/Emerald/emerald~dark.webp"),
    AppIconEntry("onyx", "Gems/Onyx/onyx.webp"),
    AppIconEntry("ruby", "Gems/Ruby/ruby.webp"),
    AppIconEntry("ruby_dark", "Gems/Ruby/ruby~dark.webp"),
    AppIconEntry("sapphire", "Gems/Sapphire/sapphire.webp"),
    AppIconEntry("sapphire_dark", "Gems/Sapphire/sapphire~dark.webp"),
    AppIconEntry("fall", "Nature/Fall/fall.webp"),
    AppIconEntry("flame", "Nature/Flame/flame.webp"),
    AppIconEntry("sakura", "Nature/Sakura/sakura.webp"),
    AppIconEntry("spring", "Nature/Spring/spring.webp"),
    AppIconEntry("summer", "Nature/Summer/summer.webp"),
    AppIconEntry("thunder", "Nature/Thunder/thunder.webp"),
    AppIconEntry("wind", "Nature/Wind/wind.webp"),
    AppIconEntry("winter", "Nature/Winter/winter.webp"),
    AppIconEntry("kurozora_coral", "Premium/Coral/kurozora_coral.webp"),
    AppIconEntry("kurozora_dutch", "Premium/Dutch Orange/kurozora_dutch.webp"),
    AppIconEntry("kurozora_green", "Premium/Green/kurozora_green.webp"),
    AppIconEntry("kurozora_ocean_blue", "Premium/Ocean Blue/kurozora_ocean_blue.webp"),
    AppIconEntry("kurozora_peach_orange", "Premium/Peach Orange/kurozora_peach_orange.webp"),
    AppIconEntry("kurozora_red", "Premium/Red/kurozora_red.webp"),
    AppIconEntry("kurozora_rose_gold", "Premium/Rose Gold/kurozora_rose_gold.webp"),
    AppIconEntry("kurozora_skye_blue", "Premium/Sky Blue/kurozora_skye_blue.webp"),
    AppIconEntry("kurozora_yellow", "Premium/Yellow/kurozora_yellow.webp"),
    AppIconEntry("kurogram", "Special Edition/Kurogram/kurogram.webp"),
    AppIconEntry("kuromorphism", "Special Edition/Kuromorphism/kuromorphism.webp"),
    AppIconEntry("kuromorphism_dark", "Special Edition/Kuromorphism/kuromorphism~dark.webp"),
    AppIconEntry("kurozora_red_special", "Special Edition/Kurozora (RED)/kurozora_red.webp"),
    AppIconEntry("mini_kuroways", "Special Edition/Mini Kuroways/mini_kuroways.webp"),
    AppIconEntry("monozora", "Special Edition/Monozora/monozora.webp"),
    AppIconEntry("strikeout", "Special Edition/Strikeout/strikeout.webp"),
    AppIconEntry("kurozora_brat", "Trends/Brat Green/kurozora_brat.webp"),
    AppIconEntry("gen_z_purple", "Trends/Gen Z Purple/gen_z_purple.webp"),
    AppIconEntry("millenial_pink", "Trends/Millenial Pink/millenial_pink.webp"),
    AppIconEntry("kurozora_local", "kurozora_local.webp"),
)

val iconsSrcDir = file("src/commonMain/composeResources/files/icons")
val generatedResDir = file("build/generated/appicon/res")
val generatedManifestDir = file("build/generated/appicon/manifest")

tasks.register("generateAppIconResources") {
    notCompatibleWithConfigurationCache("uses script-level references")
    description = "Generate mipmap resources and AndroidManifest aliases for app icon switcher"
    group = "app icon"

    inputs.dir(iconsSrcDir)
    outputs.dir(generatedResDir)
    outputs.dir(generatedManifestDir)

    doLast {
        // Clean previous outputs
        generatedResDir.deleteRecursively()
        generatedManifestDir.deleteRecursively()

        val ids = allAppIcons.map { it.id to it.relPath }

        val xxxhdpiDir = file("$generatedResDir/mipmap-xxxhdpi")
        val anydpiDir = file("$generatedResDir/mipmap-anydpi-v26")

        // ── 1. Generate mipmap resources ──
        ids.forEach { (id, relPath) ->
            val safeId = id.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val srcFile = file("$iconsSrcDir/$relPath")
            if (!srcFile.exists()) {
                throw GradleException("Icon source file not found: ${srcFile.path}")
            }

            // Pre-v26 fallback: full icon bitmap
            val destWebp = file("$xxxhdpiDir/ic_app_$safeId.webp")
            destWebp.parentFile.mkdirs()
            srcFile.copyTo(destWebp, overwrite = true)

            // Foreground layer for adaptive-icon (v26+): same full icon used as foreground
            val destForeground = file("$xxxhdpiDir/ic_app_${safeId}_foreground.webp")
            srcFile.copyTo(destForeground, overwrite = true)

            val background = "@color/ic_launcher_background"
            val foregroundRef = "@mipmap/ic_app_${safeId}_foreground"

            // Regular and round adaptive-icon wrappers (v26+)
            listOf("ic_app_$safeId", "ic_app_${safeId}_round").forEach { name ->
                val xmlFile = file("$anydpiDir/$name.xml")
                xmlFile.parentFile.mkdirs()
                xmlFile.writeText("""<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="$background" />
    <foreground android:drawable="$foregroundRef" />
</adaptive-icon>""")
            }
        }

        // ── 2. Generate AndroidManifest with aliases ──
        val srcManifest = file("src/androidMain/AndroidManifest.xml")
        val destManifest = file("$generatedManifestDir/AndroidManifest.xml")
        destManifest.parentFile.mkdirs()

        val aliasPlaceholder = "<!-- GENERATED_ALIASES -->"
        val aliasEntries = buildString {
            ids.forEach { (id, _) ->
                val safeId = id.replace(Regex("[^a-zA-Z0-9_]"), "_")
                val aliasName = "MainActivity_$safeId"
                val isDefault = id == "default"
                appendLine("""        <activity-alias
            android:name=".$aliasName"
            android:enabled="$isDefault"
            android:exported="true"
            android:icon="@mipmap/ic_app_$safeId"
            android:roundIcon="@mipmap/ic_app_$safeId"
            android:targetActivity=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity-alias>""")
            }
        }

        val manifestContent = srcManifest.readText()
        val result = manifestContent.replace(aliasPlaceholder, aliasEntries)
        destManifest.writeText(result)
    }
}

tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn("generateAppIconResources")
}

android.sourceSets.getByName("main").apply {
    res.srcDir(generatedResDir)
    manifest.srcFile(file("$generatedManifestDir/AndroidManifest.xml"))
}
