// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    }
}

plugins {
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
}

ext {
    extra["compile_sdk_version"] = 34
    extra["min_sdk_version"] = 24
    extra["target_sdk_version"] = 34
}