import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    // id("io.fabric")
}

apply(from = "${rootProject.rootDir}/tools/spotless.gradle")
apply(from = "${rootProject.rootDir}/tools/ktlint.gradle")

android {
    compileSdkVersion(28)

    val fabricPropertiesFile = rootProject.file("app/fabric.properties")
    var fabricProperties: PropertiesFile? = null
    if (fabricPropertiesFile.exists()) {
        fabricProperties = PropertiesFile(fabricPropertiesFile, "fabric.properties")
    }

    defaultConfig {
        applicationId = "com.aykuttasil.gradlekotlin"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ext["betaDistributionEmails"] = "aykuttasil@gmail.com"
        ext["betaDistributionNotifications"] = true
        resValue("string", "io.fabric.ApiKey", (fabricProperties?.getValue("apiKey") ?: "xyz") as String)
    }


    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    flavorDimensions("default")
    productFlavors {
        create("prod") {
            setDimension("default")
            resValue("string", "app_name", "MAS")
        }

        create("dev") {
            setDimension("default")
            resValue("string", "app_name", "MAS Dev")
        }

        create("mock") {
            setDimension("default")
            resValue("string", "app_name", "MAS Mock")
        }
    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    var keystoreProperties: PropertiesFile? = null
    if (keystorePropertiesFile.exists()) {
        keystoreProperties = PropertiesFile(keystorePropertiesFile, "keystore.properties")
    }
    signingConfigs {
        keystoreProperties?.let { propertiesFile ->
            getByName("debug") {
                storeFile = file(propertiesFile.getProperty("signingStoreFileDebug"))
                storePassword = propertiesFile.getProperty("signingStorePasswordDebug")
                keyAlias = propertiesFile.getProperty("signingKeyAliasDebug")
                keyPassword = propertiesFile.getProperty("signingKeyAliasPasswordDebug")
            }

            create("release") {
                storeFile = file(propertiesFile.getProperty("signingStoreFile"))
                storePassword = propertiesFile.getProperty("signingStorePassword")
                keyAlias = propertiesFile.getProperty("signingKeyAlias")
                keyPassword = propertiesFile.getProperty("signingKeyAliasPassword")
            }
        }
    }

    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")

        val commonTest = "src/commonTest/java"
        getByName("androidTest").java.srcDirs(commonTest)
        getByName("test").java.srcDirs(commonTest)
    }

    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }

    packagingOptions {
        exclude("META-INF/services/javax.annotation.processing.Processor")
        exclude("LICENSE.txt")
        exclude("META-INF/license/LICENSE.base64.txt")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/rxjava.properties")
        exclude("META-INF/MANIFEST.MF")
        exclude("META-INF/main.kotlin_module")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    dataBinding {
        isEnabled = true
    }

    androidExtensions {
        isExperimental = true
    }

    configurations.all {
        resolutionStrategy {
            force()
        }
    }

    /*
    applicationVariants.all { variant ->
        variant.outputs.all { output ->
            output.outputFile.name.replace(".apk", "-${variant.versionName}-${variant.versionCode}.apk")
            true
        }
    }
    */

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation(Libs.appcompat)
    implementation(Libs.constraintlayout)
    implementation(Libs.crashlytics) {
        isTransitive = true
    }

    testImplementation("junit:junit:4.12")
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
}
