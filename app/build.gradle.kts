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
    implementation(files("library/ContextCordinator.jar"))

    implementation(libs.play.services.maps)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation (libs.junit)
    testImplementation (libs.mockito.core)

    implementation (libs.play.services.location.v2101)
    implementation (libs.play.services.fitness)

    implementation (libs.play.services.auth)  // Google Sign-In API
    implementation ("com.google.android.gms:play-services-fitness:21.0.0") // Google Fit API
    implementation ("com.google.android.material:material:1.6.0") // For Material components like buttons

    implementation(libs.play.services.location)
    implementation (libs.pubnub.kotlin)
}