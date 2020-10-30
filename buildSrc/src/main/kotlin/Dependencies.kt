@file:Suppress("unused")

object BuildScriptDependencies {
    const val BUILD_TOOLS_GRADLE = "com.android.tools.build:gradle:${BuildScriptVersions.BUILD_TOOLS_GRADLE}"
    const val KOTLIN_GRADLE = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
    const val DAGGER_HILT_GRADLE = "com.google.dagger:hilt-android-gradle-plugin:${Versions.DAGGER_HILT}"
    const val ABOUT_LIBRARIES_GRADLE = "com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:${Versions.ABOUT_LIBRARIES}"
    const val DEXCOUNT_GRADLE = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:${BuildScriptVersions.DEXCOUNT_GRADLE}"
    const val VERSIONS_GRADLE = "com.github.ben-manes:gradle-versions-plugin:${BuildScriptVersions.VERSIONS_GRADLE}"
}

object Dependencies {
    const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN}"
    const val KOTLIN_COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.KOTLIN_COROUTINES}"

    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE}"
    const val ANDROIDX_FRAGMENT_KTX = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT}"
    const val ANDROIDX_RECYCLERVIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLERVIEW}"
    const val ANDROIDX_MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
    const val ANDROIDX_DATASTORE = "androidx.datastore:datastore-preferences:${Versions.DATASTORE}"

    const val ANDROIDX_LIFECYCLE_LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE}"

    const val DAGGER = "com.google.dagger:dagger:${Versions.DAGGER}"
    const val DAGGER_COMPILER = "com.google.dagger:dagger-compiler:${Versions.DAGGER}"
    const val DAGGER_HILT = "com.google.dagger:hilt-android:${Versions.DAGGER_HILT}"
    const val DAGGER_HILT_COMPILER = "com.google.dagger:hilt-android-compiler:${Versions.DAGGER_HILT}"
    const val DAGGER_HILT_VIEWMODEL = "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.DAGGER_HILT_VIEWMODEL}"
    const val DAGGER_HILT_VIEWMODEL_COMPILER = "androidx.hilt:hilt-compiler:${Versions.DAGGER_HILT_VIEWMODEL}"

    const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"

    const val MATERIAL_DIALOGS_CORE = "com.afollestad.material-dialogs:core:${Versions.MATERIAL_DIALOGS}"
    const val MATERIAL_DIALOGS_BOTTOMSHEETS = "com.afollestad.material-dialogs:bottomsheets:${Versions.MATERIAL_DIALOGS}"
    const val MATERIAL_DIALOGS_LIFECYCLE = "com.afollestad.material-dialogs:lifecycle:${Versions.MATERIAL_DIALOGS}"

    const val ABOUT_LIBRARIES = "com.mikepenz:aboutlibraries:${Versions.ABOUT_LIBRARIES}"
}

object DebugDependencies {
    const val BEAGLE = "com.github.pandulapeter.beagle:ui-drawer:${DebugVersions.BEAGLE}"
    const val BEAGLE_LOG = "com.github.pandulapeter.beagle:log:${DebugVersions.BEAGLE}"
}
