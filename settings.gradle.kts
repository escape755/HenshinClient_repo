// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HenshinClient"
include(":app")
