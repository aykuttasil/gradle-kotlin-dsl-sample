import org.gradle.api.tasks.testing.logging.TestExceptionFormat

buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://maven.fabric.io/public")

    }
    dependencies {
        classpath(Libs.com_android_tools_build_gradle)
        classpath(Libs.kotlin_gradle_plugin)
        classpath(Libs.io_fabric_tools_gradle)
        classpath(Libs.spotless_plugin_gradle)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }

    tasks.withType(Javadoc::class).all {
        enabled = false
    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.withType(Test::class) {
    testLogging {
        showExceptions = true
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
        events("started", "skipped", "passed", "failed")
    }
}