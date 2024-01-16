@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.tws.moments.varients-app")
    alias(libs.plugins.android.application)
    alias(libs.plugins.org.jetbrains.kotlin)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.tws.moments"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.tws.moments"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        // TODO use the extension from com.tws.moments.utils
        kotlinCompilerExtensionVersion = project
            .extensions
            .getByType(VersionCatalogsExtension::class.java)
            .named("libs")
            .findVersion("compose.compiler")
            .get()
            .requiredVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.designsystem)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    androidTestImplementation(libs.bundles.compose.android.test)
    debugImplementation(libs.bundles.compose.debug)
    implementation(libs.androidx.navigation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.gson)
    testImplementation(libs.robolectric)

    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    androidTestImplementation(libs.mockito.mockito.core)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.kotlin.mockito.kotlin)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.jakewharton)

    implementation("io.coil-kt:coil-compose:2.2.0")
}
