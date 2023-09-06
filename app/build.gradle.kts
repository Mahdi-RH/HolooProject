plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.roohandeh.holoomapproject"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.roohandeh.holoomapproject"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a")
            isUniversalApk = false
        }
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //Neshan sdk library
    implementation ("neshan-android-sdk:mobile-sdk:1.0.2")
    implementation ("neshan-android-sdk:services-sdk:1.0.0")
    implementation ("neshan-android-sdk:common-sdk:0.0.2")

    //Play Services
    implementation ("com.google.android.gms:play-services-gcm:17.0.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    // navigation component
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-alpha01")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

}