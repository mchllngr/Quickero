import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
//    id("dagger.hilt.android.plugin")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.getkeepsafe.dexcount")
}

android {
    compileSdk = rootProject.extra.get("compileSdk") as Int

    defaultConfig {
        applicationId = "de.mchllngr.quickero"
        minSdk = rootProject.extra.get("minSdk") as Int
        targetSdk = rootProject.extra.get("targetSdk") as Int
        versionCode = rootProject.extra.get("versionCode") as Int
        versionName = rootProject.extra.get("versionName") as String

        resConfigs("en", "de")
    }

//    buildFeatures {
//        viewBinding true
//    }
//
//    signingConfigs {
//        register("release") {
//            val keystorePropertiesFile = rootProject.file("keystore.properties")
//            val keystoreProperties = Properties()
//            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
//
//            storeFile = file(keystoreProperties["storeFile"] as String)
//            storePassword = keystoreProperties["storePassword"] as String
//            keyAlias = keystoreProperties["keyAlias"] as String
//            keyPassword = keystoreProperties["keyPassword"] as String
//        }
//    }
//
//    buildTypes {
//        release {
//            postprocessing {
//                removeUnusedCode true
//                removeUnusedResources true
//                obfuscate true
//                optimizeCode true
//                proguardFiles(
//                    getDefaultProguardFile("proguard-defaults.txt"),
//                    "../proguard/proguard-rules.pro",
//                    "../proguard/proguard-rules-aboutlibraries.pro",
//                    "../proguard/proguard-rules-timber.pro"
//                )
//            }
//            signingConfig signingConfigs.release
//        }
//
//        debug {
//            postprocessing {
//                removeUnusedCode false
//                removeUnusedResources false
//                obfuscate false
//                optimizeCode false
//            }
//
//            applicationIdSuffix ".debug"
//            versionNameSuffix "-debug"
//        }
//    }
//
//    dexOptions {
//        maxProcessCount 4
//        javaMaxHeapSize "3g"
//    }
//
//    compileOptions {
//        sourceCompatibility JavaVersion.VERSION_1_8
//        targetCompatibility JavaVersion.VERSION_1_8
//    }
//
//    kotlinOptions {
//        jvmTarget = "1.8"
//        freeCompilerArgs += [
//            "-Xjvm-default=compatibility"
//        ]
//    }
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.kotlinCoroutines)

//    implementation(libs.androidx.core)
//    implementation(libs.androidx.fragment)
//    implementation(libs.androidx.recyclerview)
//    implementation(libs.androidx.datastore)
//    implementation(libs.androidx.lifecycle.livedata)

    implementation(libs.material)

    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    implementation(libs.daggerHilt)
    kapt(libs.daggerHiltCompiler)

    implementation(libs.timber)

//    implementation(libs.bundles.materialDialogs)

    implementation(libs.aboutLibraries)

    debugImplementation(libs.bundles.debug.beagle)
}
