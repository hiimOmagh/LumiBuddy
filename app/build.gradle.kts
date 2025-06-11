plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "de.omagh.lumibuddy"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.omagh.lumibuddy"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Material Design
    implementation(libs.material)
    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    // CameraX
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    // Guava needed for CameraX ListenableFuture
    implementation(libs.guava)
    implementation(libs.jetbrains.annotations)

    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
// For LiveData support:
    implementation(libs.androidx.room.ktx)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}