rootProject.name = "媒体盒子"

include(":app")
include(":skin")
include(":skin_dark")
include(":skin_blue")
include(":skin_lemon")
include(":skin_sweat_soybean")
include(":skin_dark_sakura")
val pluginApi = ":MediaBoxPluginApi"
include(pluginApi)
project(pluginApi).projectDir = File("./submodules/MediaBoxPlugin/pluginApi")