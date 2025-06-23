plugins {
    id("java-library")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
dependencies {
    implementation(libs.androidx.annotation.jvm)
    implementation(libs.androidx.room.common.jvm)
    // no AndroidX here—just pure-Java dependencies if needed
}