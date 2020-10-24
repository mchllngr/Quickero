@file:Suppress("unused")

object BuildScriptDependencies {
    const val BUILD_TOOLS_GRADLE = "com.android.tools.build:gradle:${BuildScriptVersions.BUILD_TOOLS_GRADLE}"
    const val KOTLIN_GRADLE = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
    const val DEXCOUNT_GRADLE = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:${BuildScriptVersions.DEXCOUNT_GRADLE}"
    const val VERSIONS_GRADLE = "com.github.ben-manes:gradle-versions-plugin:${BuildScriptVersions.VERSIONS_GRADLE}"
}

object Dependencies {
    const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN}"
    const val KOTLIN_COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.KOTLIN_COROUTINES}"

    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE}"
    const val ANDROIDX_FRAGMENT_KTX = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT}"
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
    const val ANDROIDX_RECYCLERVIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLERVIEW}"
    const val ANDROIDX_MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
    const val ANDROIDX_CONSTRAINTLAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    const val ANDROIDX_PREFERENCE = "androidx.preference:preference:${Versions.PREFERENCE}"

    const val ANDROIDX_LIFECYCLE_EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:${Versions.LIFECYCLE}"
    const val ANDROIDX_LIFECYCLE_COMMON = "androidx.lifecycle:lifecycle-common-java8:${Versions.LIFECYCLE}"
    const val ANDROIDX_LIFECYCLE_LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE}"
    const val ANDROIDX_LIFECYCLE_VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE}"

    const val MOSBY = "com.hannesdorfmann.mosby3:mvp:${Versions.MOSBY}"

    const val BUTTERKNIFE = "com.jakewharton:butterknife:${Versions.BUTTERKNIFE}"
    const val BUTTERKNIFE_COMPILER = "com.jakewharton:butterknife-compiler:${Versions.BUTTERKNIFE}"

    const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"

    const val GSON = "com.google.code.gson:gson:${Versions.GSON}"

    const val RX_JAVA = "io.reactivex:rxjava:${Versions.RX_JAVA}"
    const val RX_ANDROID = "io.reactivex:rxandroid:${Versions.RX_ANDROID}"

    const val RX_PREFERENCES = "com.f2prateek.rx.preferences:rx-preferences:${Versions.RX_PREFERENCES}"

    const val MATERIAL_DIALOGS_CORE = "com.afollestad.material-dialogs:core:${Versions.MATERIAL_DIALOGS}"
    const val MATERIAL_DIALOGS_BOTTOMSHEETS = "com.afollestad.material-dialogs:bottomsheets:${Versions.MATERIAL_DIALOGS}"
    const val MATERIAL_DIALOGS_LIFECYCLE = "com.afollestad.material-dialogs:lifecycle:${Versions.MATERIAL_DIALOGS}"

    const val SIMPLE_ITEM_DECORATION = "com.bignerdranch.android:simple-item-decoration:${Versions.SIMPLE_ITEM_DECORATION}"

    const val ATTRIBOUTER = "me.jfenn:attribouter:${Versions.ATTRIBOUTER}"
}

object DebugDependencies {
    const val BEAGLE = "com.github.pandulapeter.beagle:ui-drawer:${DebugVersions.BEAGLE}"
    const val BEAGLE_LOG = "com.github.pandulapeter.beagle:log:${DebugVersions.BEAGLE}"
}
