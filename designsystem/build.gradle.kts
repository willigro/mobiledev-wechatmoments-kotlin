@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.tws.moments.library")
    id("com.tws.moments.android-compose")
}

android {
    namespace = "com.tws.moments.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}