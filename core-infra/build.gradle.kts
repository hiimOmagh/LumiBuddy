plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "de.omagh.core_infra"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 28

        buildConfigField(
            "String",
            "PLANT_ID_API_KEY",
            "\"${project.findProperty("PLANT_ID_API_KEY") ?: ""}\""
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
    implementation(project(":core-data"))
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.dagger)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.guava)
    implementation(libs.androidx.activity)
    implementation(libs.appcompat)
    implementation(libs.work.runtime)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.rxjava3)
    implementation(libs.rxandroid3)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.tensorflow.lite)
    implementation(libs.arcore)
    implementation(libs.timber)
    implementation(libs.sceneform.core)
    implementation(libs.sceneform.ux)
    compileOnly(libs.jspecify)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.work.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
