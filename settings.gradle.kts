pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    // Компиляторный плагин Kotlin (serialization) в catalog объявлен без версии.
    // Под built-in Kotlin версия задаётся здесь один раз и совпадает с KGP, который несёт AGP 9.3.1.
    plugins {
        id("org.jetbrains.kotlin.plugin.serialization") version "2.2.10"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "UzGidro News"
include(":app")
