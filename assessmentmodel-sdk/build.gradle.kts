plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android")
    kotlin("plugin.serialization")
    id ("maven-publish")
    id("org.jetbrains.dokka")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        val versionCode = extra["sdkVersionCode"]
        val versionName = extra["versionName"]
        buildConfigField("int", "VERSION_CODE", "${versionCode}")
        buildConfigField("String", "VERSION_NAME", "\"${versionName}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}


dependencies {
        val assessmentVersion = "0.12.0"
        api(project(":bridge-client"))
        api("org.sagebionetworks.assessmentmodel:assessmentModel:$assessmentVersion")

        api(libs.koin.android)
        // Kermit
        implementation(libs.touchlab.kermit)

        // legacy Bridge dependencies
        // migrate to use kotlinx/java8 time and see if we can publish multi-platform
        // which will work with bridge - liujoshua 04/02/21
        implementation("org.sagebionetworks:BridgeDataUploadUtils:0.2.6") {
            exclude("joda-time", "joda-time")
            exclude("org.bouncycastle")
            exclude("com.madgag.spongycastle")
        }
        api("net.danlew:android.joda:2.9.9.4")
        val spongycastle = "1.58.0.0"
        implementation("com.madgag.spongycastle:core:$spongycastle")
        implementation("com.madgag.spongycastle:prov:$spongycastle")
        // marked api due to propagation of CMSException
        api("com.madgag.spongycastle:bcpkix-jdk15on:$spongycastle")

        testImplementation("black.ninia:jep:4.1.1")
    }

tasks.withType(Test::class) {
    //val path = System.getProperty("user.dir") + "/python-packages/jep"
    val path = "/Library/Frameworks/Python.framework/Versions/3.11/lib/python3.11/site-packages/jep"
    systemProperty("java.library.path", path)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("assessmentmodel-sdk") {
                from(components.getByName("release"))
            }
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://sagebionetworks.jfrog.io/artifactory/mobile-sdks/")
            credentials {
                username = System.getenv("artifactoryUser")
                password = System.getenv("artifactoryPwd")
            }
        }
    }
}

