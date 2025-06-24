plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "de.omagh.feature_growschedule"
    compileSdk = 35

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    implementation(project(":core-infra"))
    implementation(project(":core-data"))
    implementation(libs.rxjava2)
    implementation(libs.rxandroid2)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(project(":core-domain"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}