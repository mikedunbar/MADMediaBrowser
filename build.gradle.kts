// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {

    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.42")
    }
}

tasks.register("clean").configure {
    delete(rootProject.buildDir)
}