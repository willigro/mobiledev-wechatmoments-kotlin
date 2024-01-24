// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.detekt)
}
true // Needed to make the Suppress annotation work for the plugins block

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

allprojects {
        apply {
            plugin("io.gitlab.arturbosch.detekt")
        }

        detekt {
            debug = true
            parallel = true
            allRules = true
            autoCorrect = true
            buildUponDefaultConfig = true
            config.from(rootProject.files("${rootProject.rootDir}/config/detekt/detekt.yml"))
        }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    println("naming=$name")
    include("**/*.kt")
    exclude("**/build/**")
    exclude("**/build-logic/**")
    exclude("**/buildlogic/**")
    jvmTarget = JavaVersion.VERSION_17.toString()

    reports {
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
        html.required.set(true)
        html.outputLocation.set(file("${project.layout.buildDirectory.get()}/reports/detekt/detekt.html"))
        xml.required.set(true) // It's required for Sonar
        xml.outputLocation.set(file("${project.layout.buildDirectory.get()}/reports/detekt/detekt.xml"))
    }
}