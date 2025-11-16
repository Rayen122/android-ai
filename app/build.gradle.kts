plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.androidapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.androidapplication"
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    implementation(libs.retrofit)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.lottie.compose)
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0") // Add this line
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
// Or the latest stable version
    // AndroidX
    implementation(libs.bundles.androidX)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.androidx.foundation)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //TODO 2 Add ROOM dependencies here
    implementation("androidx.room:room-runtime:2.5.2")
    annotationProcessor("androidx.room:room-compiler:2.5.2")
    implementation(platform("androidx.compose:compose-bom:2024.06.00")) // Utilisez votre version de la BOM
    implementation ("androidx.compose.material:material-icons-extended:<version>")
    implementation ("androidx.compose.ui:ui-text") // <-- THIS IS CRUCIAL FOR KeyboardOptions

    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.5.0") // Ensure this is added for observeAsState
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
   // implementation ("androidx.core:core-ktx:1.9.0") // Check this version
   // implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("androidx.compose.foundation:foundation:1.5.0")  // Make sure you have this or a similar version
// Add this line
    implementation("androidx.compose.ui:ui:1.6.0") // Pour utiliser les fonctionnalités Canvas et DrawScope
    implementation("androidx.compose.foundation:foundation:1.6.0") // Pour Canvas
    implementation("androidx.compose.material3:material3:1.2.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("androidx.security:security-crypto:1.0.0")
//exo player

        implementation(platform(libs.compose.bom))
    debugImplementation(libs.compose.tooling)
    implementation(libs.bundles.ui)
}
