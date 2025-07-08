plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "de.omagh.core_infra"
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

    implementation(project(":core-domain"))
    implementation(project(":core-data"))
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.dagger)
    implementation(libs.camera.core)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.androidx.activity)
    implementation(libs.appcompat)
    implementation(libs.work.runtime)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.rxjava2)
    implementation(libs.rxandroid2)
    implementation(libs.timber)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
