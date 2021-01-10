plugins {
    kotlin("jvm") version "1.4.21"
    maven
}

group = "com.github.xjcyan1de"
version = "1.5"

repositories {
    jcenter()
    maven { setUrl("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.4-R0.1-SNAPSHOT")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.freeCompilerArgs = listOf(
                "-Xopt-in=kotlin.RequiresOptIn"
        )
    }
}