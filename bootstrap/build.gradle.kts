plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.graalvm)
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
    implementation(libs.netty)

    // SSL
    implementation(libs.bouncy.castle.pkix)
    implementation(libs.netty.tcnative.boringssl.static)
    implementation("${libs.netty.tcnative.boringssl.static.get().module}:${libs.netty.tcnative.boringssl.static.get().version}:windows-x86_64")
    implementation("${libs.netty.tcnative.boringssl.static.get().module}:${libs.netty.tcnative.boringssl.static.get().version}:linux-x86_64")
    implementation("${libs.netty.tcnative.boringssl.static.get().module}:${libs.netty.tcnative.boringssl.static.get().version}:linux-aarch_64")
    implementation("${libs.netty.tcnative.boringssl.static.get().module}:${libs.netty.tcnative.boringssl.static.get().version}:osx-x86_64")
    implementation("${libs.netty.tcnative.boringssl.static.get().module}:${libs.netty.tcnative.boringssl.static.get().version}:osx-aarch_64")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.nookure.sync.Boostrap"
        )
    }
}

tasks.shadowJar {
    manifest {
        attributes(
            "Main-Class" to "com.nookure.sync.Bootstrap"
        )
    }

    archiveFileName.set("nookure-sync-boot-${rootProject.version}.jar")
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("nookure-sync-boot")
            mainClass.set("com.nookure.sync.Bootstrap")
            buildArgs("-04")
        }
    }
}