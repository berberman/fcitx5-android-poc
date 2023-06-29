@file:Suppress("UnstableApiUsage")

plugins {
    id("android-app-convention")
    id("android-plugin-app-convention")
    id("build-metadata")
    id("data-descriptor")
}

android {
    namespace = "org.fcitx.fcitx5.android.plugin.clipboard"

    defaultConfig {
        applicationId = "org.fcitx.fcitx5.android.plugin.clipboard"
    }

    buildTypes {
        release {
            resValue("string", "app_name", "@string/app_name_release")
        }
        debug {
            resValue("string", "app_name", "@string/app_name_debug")
        }
    }
}

dependencies {
    implementation(project(":lib:plugin-base"))
}
