
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.dcis2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dcis2"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["googleApiKey"] = project.findProperty("GOOGLE_API_KEY") ?: ""

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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.camera.view)
    implementation (libs.zxing.android.embedded)
    implementation (libs.androidx.appcompat.v131)
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.location)

    implementation(libs.play.services.maps)
    implementation(files("/Users/mac/Documents/DCIS/app/library/ContextCordinator 1.0 DEV_New.jar"))
    // Manually exclude dependencies from being added separately
    configurations.all {
        exclude("org.json","json")
        exclude("com.google.code.gson","gson")
        exclude("com.google.errorprone","error_prone_annotations")
        exclude("com.google.guava","listenablefuture")
    }

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation (libs.junit)
    testImplementation (libs.mockito.core)

    implementation (libs.play.services.location.v2101)
    implementation (libs.play.services.fitness)

    implementation(libs.play.services.location)
    implementation (libs.pubnub.kotlin)

}