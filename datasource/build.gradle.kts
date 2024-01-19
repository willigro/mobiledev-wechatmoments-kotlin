@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.tws.moments.library")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.tws.moments.datasource"
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.gson)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.jakewharton)

    androidTestImplementation(libs.bundles.android.test)
    testImplementation(libs.bundles.test)
}