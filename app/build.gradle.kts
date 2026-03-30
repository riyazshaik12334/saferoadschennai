plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.simats.saferoadschennaisrc"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.simats.saferoadschennaisrc"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load ANDROID_SECRET_KEY from .env
        val envFile = rootProject.file(".env")
        val androidSecretKey = if (envFile.exists()) {
            val properties = java.util.Properties()
            envFile.inputStream().use { properties.load(it) }
            properties.getProperty("ANDROID_SECRET_KEY") ?: "DEFAULT_RECOVERY_KEY_2026"
        } else {
            "DEFAULT_RECOVERY_KEY_2026"
        }
        buildConfigField("String", "ANDROID_SECRET_KEY", "\"$androidSecretKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.androidx.swiperefreshlayout)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
