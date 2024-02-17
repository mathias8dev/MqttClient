plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

kotlin {
    sourceSets {
        debug {
            kotlin.srcDir("build/generated/ksp/debug/kotlin")
        }
        release {
            kotlin.srcDir("build/generated/ksp/release/kotlin")
        }
    }
}

android {
    namespace = "com.mathias8dev.mqttclient"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mathias8dev.mqttclient"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    hilt {
        enableAggregatingTask = true
    }
}


dependencies {

    // Kotlin reflect
    val kotlinVersion = "1.9.22"
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")


    // Vico

    val vicoVersion = "2.0.0-alpha.6"
    implementation("com.patrykandpatrick.vico:compose:$vicoVersion")
    implementation("com.patrykandpatrick.vico:compose-m3:$vicoVersion")
    implementation("com.patrykandpatrick.vico:core:$vicoVersion")

    // Kotlin-yup

    val kotlinYupVersion = "1.0.1"
    implementation("com.github.mathias8dev:kotlin-yup:$kotlinYupVersion")

    // lifecycle-service

    val lifecycleServiceVersion = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleServiceVersion")

    // workManager

    val workManagerVersion = "2.9.0"
    implementation("androidx.work:work-runtime-ktx:$workManagerVersion")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")


    // Room

    val roomVersion = "2.6.1"

    implementation ("androidx.room:room-ktx:$roomVersion")
    implementation ("androidx.room:room-runtime:$roomVersion")
    // To use Kotlin Symbol Processing (KSP)
    ksp ("androidx.room:room-compiler:$roomVersion")

    // MQTT
    // https://mvnrepository.com/artifact/org.eclipse.paho/org.eclipse.paho.client.mqttv3
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")

    val lifecycleVersion  = "2.7.0"
    val lottieVersion = "6.2.0"

    // LottieAnimation
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    // Custom Material3 AlertDialog
    implementation("androidx.compose.material3:material3:1.1.2")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation( "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    // State Management (collectAsStateWithLifecycle) and others
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    // Accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")

    // Datastore
    implementation("androidx.datastore:datastore:1.1.0-beta01")


    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // ComposeDestination
    implementation("io.github.raamcosta.compose-destinations:core:1.9.54")

    // Coil
    implementation("io.coil-kt:coil-compose:2.4.0")


    // Hilt
    implementation ("com.google.dagger:hilt-android:2.48.1")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")

    ksp("io.github.raamcosta.compose-destinations:ksp:1.9.54")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

    // Default
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}


kapt {
    correctErrorTypes = true
}