plugins {
    id("java-library")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
dependencies {
    implementation(libs.rxjava2)
    implementation(libs.dagger)
    // no AndroidX here—just pure-Java dependencies if needed
}