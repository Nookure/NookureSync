dependencies {
  compileOnlyApi(libs.config.lib.core)
  compileOnlyApi(libs.config.lib.yaml)
  compileOnlyApi(libs.netty)
  testImplementation(libs.config.lib.core)
  testImplementation(libs.config.lib.yaml)
}