// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.42")
    }
}

ext {
    extra["compile_sdk_version"] = 33
    extra["min_sdk_version"] = 23
    extra["target_sdk_version"] = 33
}

tasks.register("clean").configure {
    delete(rootProject.buildDir)
}