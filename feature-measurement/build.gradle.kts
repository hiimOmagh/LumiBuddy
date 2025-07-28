plugins {
    alias(libs.plugins.android.library)
}

apply(from = rootProject.file("gradle/packaging-options.gradle"))

android {
    namespace = "de.omagh.feature_measurement"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 28

        buildConfigField(
            "String",
            "GROW_LIGHT_API_KEY",
            "\"${project.findProperty("GROW_LIGHT_API_KEY") ?: ""}\""
        )

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
    implementation(project(":shared-ml"))
    implementation(libs.dagger)
    implementation(libs.work.runtime)
    implementation(libs.arcore)
    implementation(libs.androidx.fragment.testing)
    implementation(libs.ext.junit)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.timber)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.guava)
    implementation(libs.rxjava3)
    implementation(libs.rxandroid3)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    compileOnly(libs.jspecify)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.androidx.fragment.testing)
    androidTestImplementation(libs.espresso.core)
}
