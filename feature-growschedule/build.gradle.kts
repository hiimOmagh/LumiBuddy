plugins {
    alias(libs.plugins.android.library)
}

apply(from = rootProject.file("gradle/packaging-options.gradle"))

android {
    namespace = "de.omagh.feature_growschedule"
    compileSdk = 36

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
    implementation(libs.rxjava3)
    implementation(libs.rxandroid3)
    implementation(libs.appcompat)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.recyclerview)
    implementation(libs.activity)
    implementation(libs.material)
    implementation(libs.work.runtime)
    implementation(libs.androidx.navigation.runtime.android)
    compileOnly(libs.jspecify)
    implementation(project(":core-domain"))
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.androidx.test.core)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
