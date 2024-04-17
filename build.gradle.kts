// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.apply {
        set("nav_version", "2.5.3")
        set("room_version", "2.5.1")
        set("compose_version", "1.0.5")
    }
    val agp_version by extra("8.1.2")
    val agp_version1 by extra("8.0.0")
    val agp_version2 by extra("8.1.2")
    val agp_version3 by extra("8.2.0")
}
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}
