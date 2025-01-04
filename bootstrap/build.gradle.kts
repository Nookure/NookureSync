plugins {
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

dependencies {
    // Logging
    implementation(libs.slf4j.api)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j.impl)

    // Config
    implementation(libs.config.lib.yaml)

    // Inject
    implementation(libs.guice)

    // Subprojects
    implementation(project(":common"))
    implementation(project(":server"))

    // Network
    implementation(libs.jzlib)

    // SSL
    implementation(libs.bouncy.castle.pkix)
}
