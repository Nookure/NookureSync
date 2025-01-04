plugins {
  id("java")
}

group = "com.nookure.sync"
version = "1.0.0"

tasks.test {
  useJUnitPlatform()
}

allprojects {
  apply<JavaPlugin>()
  apply(plugin = "java-library")

  repositories {
    mavenCentral()
  }

  dependencies {
    compileOnly(rootProject.libs.guice)
    compileOnly(rootProject.libs.guice.assistedinject)
    compileOnly(rootProject.libs.google.auto.value.annotations)
    annotationProcessor(rootProject.libs.google.auto.value.processor)
    compileOnly(rootProject.libs.google.auto.service.annotations)
    annotationProcessor(rootProject.libs.google.auto.service)

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.jetbrains:annotations:26.0.1")
  }

  tasks {
    withType<JavaCompile> {
      options.encoding = "UTF-8"
    }

    withType<Javadoc> {
      options.encoding = "UTF-8"
    }
  }

  tasks.test {
    useJUnitPlatform()
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
  }
}