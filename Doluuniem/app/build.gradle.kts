plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.shoestore"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.shoestore"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true;
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
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
   // implementation(files("C:\\Users\\ADMIN\\Desktop\\ZaloPay\\zpdk-release-v3.1.aar"))
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //add
    implementation(libs.glide)
    //noinspection KaptUsageInsteadOfKsp
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.okhttp)

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.google.firebase:firebase-analytics")
    //authentication
    implementation("com.google.firebase:firebase-auth")
    // firebase store
    implementation("com.google.firebase:firebase-firestore-ktx:24.9.1")

    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    // zl pay
    implementation(fileTree(mapOf(
        "dir" to "D:\\PRM392\\ShoeStore\\ShoeStore\\app\\src\\main\\java\\com\\example\\shoestore\\Lib\\zpdk-release-v3.1.aar",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    implementation("commons-codec:commons-codec:1.14")
}