buildscript {
    repositories {
        google()
        gradlePluginPortal()
    }

    dependencies {
        // needed until https://github.com/gradle/gradle/issues/16958 is fixed
        val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs") as org.gradle.accessors.dm.LibrariesForLibs

        classpath(libs.androidPlugin)
        classpath(libs.kotlinPlugin)
        classpath(libs.daggerHiltPlugin)
        classpath(libs.aboutLibrariesPlugin)
        classpath(libs.dexcountPlugin)
        classpath(libs.versionsPlugin) // allows checking for new versions with './gradlew dependencyUpdates -Drevision=release'
    }
}

subprojects {
    apply(plugin = "com.github.ben-manes.versions")
}

extra.apply {
    set("compileSdk", 30)
    set("targetSdk", 30)
    set("minSdk", 21)

    // Injected Variables
    val major = getIntProperty("major") ?: 1
    val minor = getIntProperty("minor") ?: 3
    val hotfix = getIntProperty("hotfix") ?: 0
    val build = (getIntProperty("build") ?: 0) % 1000

    if (major < 0 || major > 99) {
        throw IllegalArgumentException("major must be 0-99")
    }
    if (minor < 0 || minor > 99) {
        throw IllegalArgumentException("minor must be 0-99")
    }
    if (hotfix < 0 || hotfix > 99) {
        throw IllegalArgumentException("hotfix must be 0-99")
    }
    if (build < 0) {
        throw IllegalArgumentException("build must be 0 or bigger")
    }

    val versionCode = major * 10000000 + minor * 100000 + hotfix * 1000 + build
    val versionName = "$major.$minor.$hotfix.$build"

    set("versionCode", versionCode)
    set("versionName", versionName)

    println("##### (Injected) variables #####")
    println("MAJOR: $major")
    println("MINOR: $minor")
    println("HOTFIX: $hotfix")
    println("BUILD: $build")
    println("VERSIONCODE: $versionCode")
    println("VERSIONNAME: $versionName")
}

fun getIntProperty(name: String) : Int? = if (project.hasProperty(name)) project.property(name) as? Int else null

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

//task<Wrapper>("wrapper") {
//    distributionType = Wrapper.DistributionType.ALL
//}
