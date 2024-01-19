@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.tws.moments.library")
}

android {
    namespace = "com.tws.moments.datasource"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}