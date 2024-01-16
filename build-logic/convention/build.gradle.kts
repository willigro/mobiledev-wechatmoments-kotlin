plugins {
    `kotlin-dsl`
}

group = "com.tws.moments.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.kotlin.gradle)
}
