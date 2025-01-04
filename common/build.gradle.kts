dependencies {
  compileOnlyApi(libs.config.lib.core)
  compileOnlyApi(libs.config.lib.yaml)
  compileOnlyApi(libs.netty)
  compileOnly(libs.slf4j.api)
  testImplementation(libs.config.lib.core)
  testImplementation(libs.config.lib.yaml)
  testImplementation(libs.guice)
}