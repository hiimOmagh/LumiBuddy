plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "de.omagh.lumibuddy"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "de.omagh.lumibuddy"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "PLANT_ID_API_KEY",
            "\"${project.findProperty("PLANT_ID_API_KEY") ?: ""}\""
        )
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
    implementation(project(":core-domain"))
    implementation(project(":core-data"))
    implementation(project(":core-infra"))
    implementation(project(":feature-measurement"))
    implementation(project(":feature-plantdb"))
    implementation(project(":feature-diary"))
    implementation(project(":feature-growschedule"))

    implementation(libs.image.labeling.custom)

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
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // Guava needed for CameraX ListenableFuture
    implementation(libs.guava)
    implementation(libs.jetbrains.annotations)

    // Room database
    implementation(libs.room.runtime)
    implementation(libs.work.runtime)
    annotationProcessor(libs.room.compiler)

    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.rxjava3)
    implementation(libs.rxandroid3)
    implementation(libs.timber)
    implementation(libs.glide)
    implementation(libs.mpandroidchart)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    debugImplementation(libs.leakcanary)
// For LiveData support:

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    compileOnly(libs.jspecify)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.navigation.testing)
}
