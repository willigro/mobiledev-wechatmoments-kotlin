import com.tws.moments.utils.getIntVersion
import com.tws.moments.utils.versionCatalog

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(jdkVersion = project.versionCatalog.getIntVersion("java"))
}
