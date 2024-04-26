plugins {
    id("com.android.application")
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
//    id("kotlin-kapt")
}

android {
    namespace = "com.example.sirius"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sirius"
        minSdk = 25
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

val coilCompose = "2.4.0"
val activityCompose = "2.4.0"
val activityCompose2 = "1.8.0"
val activityCompose3 = "1.7.2"
val activityCompose4 = "1.3.0"
val coreKtx = "1.10.1"
val lifecycleBase = "2.6.1"
val viewmodelCompose = "2.6.0"
val lifecycle = "2.6.2"
val benchmark = "1.2.0"
val buildTools = "2.9.9"
val recyclerView = "1.3.2"
val livedata = "1.3.2"
val fragment = "1.6.2"
val splashScreen = "1.0.1"
val gson = "2.8.9"
val location = "21.0.1"
val mapsCompose = "2.9.0"
val mapsService = "18.1.0"
val material = "1.0.0"
val constraintLayout = "1.0.0-alpha07"
val mockitoVersion = "4.5.1"
val espressoCoreVersion = "3.5.1"
val junitVersion = "1.1.5"
dependencies {

    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation("androidx.activity:activity-compose:$activityCompose3")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleBase")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleBase")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:$viewmodelCompose")
    implementation("androidx.activity:activity-compose:$activityCompose4")
    // Nav
    implementation("androidx.navigation:navigation-compose:${rootProject.extra["nav_version"]}")

    // Room
    implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")
    implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
    implementation("androidx.benchmark:benchmark-macro:$benchmark")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:$buildTools")
    implementation("androidx.recyclerview:recyclerview:$recyclerView")
    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:$lifecycle")
    implementation("androidx.compose.runtime:runtime-livedata:$livedata")

    // Fragment
    implementation("androidx.fragment:fragment-ktx:$fragment")

    // Activity
    implementation("androidx.activity:activity-compose:$activityCompose2")

    //Splash
    implementation("androidx.core:core-splashscreen:$splashScreen")

    // Testing
    testImplementation("io.mockk:mockk:$mockitoVersion")
    androidTestImplementation("androidx.test.sespresso:espresso-core:$espressoCoreVersion")
    androidTestImplementation("androidx.test.ext:junit:$junitVersion")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${rootProject.extra["compose_version"]}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${rootProject.extra["compose_version"]}")

    // JSON
    implementation("com.google.code.gson:gson:$gson")

    // Google Services & Maps
    implementation("com.google.android.gms:play-services-location:$location")
    implementation("com.google.maps.android:maps-compose:$mapsCompose")
    implementation("com.google.android.gms:play-services-maps:$mapsService")
    implementation("androidx.compose.material:material:$material")

    //image
    implementation("io.coil-kt:coil-compose:$coilCompose")

    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation ("androidx.constraintlayout:constraintlayout-compose:$constraintLayout")

    implementation("io.coil-kt:coil-compose:$coilCompose")
}