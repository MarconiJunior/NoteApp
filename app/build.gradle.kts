plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.marconi.noteapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.marconi.noteapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.12")
    val composeVersion = "1.6.2"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.compose.ui:ui:${composeVersion}")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material3:material3:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    testImplementation("junit:junit:")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")

    // Compose dependencies

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0-alpha02")
    implementation("androidx.navigation:navigation-compose:2.8.0-alpha03")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.material3:material3-icons-extended:$composeVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Coroutines

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Dagger - Hilt

    implementation("com.google.dagger:hilt-android:2.38.1")
    ksp("com.google.dagger:hilt-android-compiler:2.37")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Room

    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Kotlin Extensions and Coroutines support for Room

    implementation("androidx.room:room-ktx:2.6.1")
}