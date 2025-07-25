plugins {
    alias(libs.plugins.android.library)
}

apply(from = rootProject.file("gradle/packaging-options.gradle"))

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
    packagingOptions {
        jniLibs {
            pickFirsts += setOf("**/com/google/flatbuffers/**")
        }
        resources {
            pickFirsts += setOf("**/com/google/flatbuffers/**")
        }
    }
}

dependencies {
    implementation(project(":core-domain"))
    implementation(project(":core-infra"))
    implementation(project(":core-data"))
    implementation(libs.arcore)
    implementation(libs.sceneform.core) {
        exclude("com.google.flatbuffers", "flatbuffers-java")
    }
    implementation(libs.sceneform.ux) {
        exclude("com.google.flatbuffers", "flatbuffers-java")
    }
    implementation(libs.dagger)
    implementation(libs.camera.view)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.rxjava3)
    implementation(libs.rxandroid3)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.timber)
    compileOnly(libs.jspecify)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}