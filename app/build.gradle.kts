plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

val composeVersion by extra("1.3.2")
val composeMaterialVersion by extra("1.3.1")
val hiltVersion by extra("2.44.2")
val retrofitVersion by extra("2.9.0")
val moshiVersion by extra("1.13.0")
val coroutinesVersion by extra("1.5.2")
val ktxVersion by extra("2.5.1")

android {
    compileSdk = rootProject.extra["compile_sdk_version"] as Int

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    namespace = "dunbar.mike.musicbrowser"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    defaultConfig {
        applicationId = "dunbar.mike.musicbrowser"
        minSdk = rootProject.extra["min_sdk_version"] as Int
        targetSdk = rootProject.extra["target_sdk_version"] as Int
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    flavorDimensions += "tier"
    productFlavors {
        create("free") {
            dimension = "tier"
            applicationId = "dunbar.mike.musicbrowser.free"
            versionNameSuffix = "-free"
        }

        create("paid") {
            dimension = "tier"
            applicationId = "dunbar.mike.musicbrowser.paid"
            versionNameSuffix = "-paid"
        }
    }
}

dependencies {
    // Upgrading this requires updating the android gradle plugin, so let's hold off
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.2")

    implementation("androidx.core:core-splashscreen:1.0.0")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeMaterialVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeMaterialVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.navigation:navigation-compose:2.6.0-alpha04")
    implementation("androidx.activity:activity-compose:1.6.1")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$ktxVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$ktxVersion")

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    //region OkHttp
    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.0"))

    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    //endregion

    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")


    // Moshi
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")

    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}