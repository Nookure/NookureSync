dependencies {
  api(libs.netty)
  api(libs.config.lib.yaml)
  api(libs.slf4j.api)
  implementation(project(":common"))

  testImplementation(libs.netty)
  testImplementation(libs.config.lib.yaml)
  testImplementation(libs.slf4j.api)
  testImplementation(libs.guice)
  testImplementation(libs.guice.assistedinject)
  testImplementation(libs.jzlib)
  testImplementation(libs.slf4j.api)
  testImplementation(libs.log4j.core)
  testImplementation(libs.log4j.slf4j.impl)
  testImplementation(project(":common"))
}