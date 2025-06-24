plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "de.omagh.feature_diary"
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

    implementation(project(":core-data"))
    implementation(project(":core-infra"))
    implementation(project(":core-domain"))
    implementation(":feature_plantdb")
    implementation(libs.rxjava2)
    implementation(libs.rxandroid2)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(project(":feature-plantdb"))
    implementation(project(":feature-ar"))
    implementation(project(":feature-measurement"))
    implementation(project(":feature-growschedule"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}