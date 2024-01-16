import com.tws.moments.utils.getBundle
import org.gradle.kotlin.dsl.dependencies
import com.tws.moments.utils.getLibrary
import com.tws.moments.utils.getVersion
import com.tws.moments.utils.versionCatalog

plugins {
    id("com.android.library")
}

val catalog = project.versionCatalog
android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = catalog.getVersion("compose.compiler")
    }
}

dependencies {
    // TODO (rittmann):
    //  IT MUST BE debugImplementation, but I'm stuck in an error that does not recognize the
    //  STStub as debug so it cannot work as debugImplementation
    implementation(catalog.getBundle("compose.debug"))

    implementation(catalog.getLibrary("androidx.constraintlayout.compose"))
    implementation(catalog.getLibrary("androidx.activity.compose"))
    implementation(platform(catalog.getLibrary("androidx.compose.bom")))
    implementation(catalog.getBundle("compose"))
    implementation(catalog.getLibrary("androidx.navigation"))
    implementation(catalog.getLibrary("androix.compose.foundation"))

    androidTestImplementation(platform(catalog.getLibrary("androidx.compose.bom")))
    androidTestImplementation(catalog.getBundle("compose.android.test"))
}
