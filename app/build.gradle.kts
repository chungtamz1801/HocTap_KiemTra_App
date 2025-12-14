plugins {
    alias(libs.plugins.android.application) // This already applies the android plugin
    id("com.google.gms.google-services")    // Keep this for Firebase
}

android {
    namespace = "com.example.chatapp"
    compileSdk = 35 // I recommend changing 36 to 35 for stability, as 36 is preview

    defaultConfig {
        applicationId = "com.example.chatapp"
        minSdk = 24
        targetSdk = 35 // Match compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // --- THÊM DÒNG NÀY ĐỂ SỬA LỖI ---
    implementation("com.google.firebase:firebase-firestore")

    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
}