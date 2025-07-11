plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "de.omagh.feature_ar"
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
    implementation(project(":core-infra"))
    implementation(project(":core-data"))
    implementation(libs.arcore)
    implementation(libs.sceneform.core)
    implementation(libs.sceneform.ux)
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.rxjava3)
    implementation(libs.rxandroid3)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    compileOnly(libs.jspecify)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}