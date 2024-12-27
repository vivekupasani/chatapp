plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services") // Added for Firebase
}

android {
    namespace = "com.vivekupasani.chatapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vivekupasani.chatapp"
        minSdk = 24
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // OTP view
    implementation("com.github.mukeshsolanki:android-otpview-pinview:2.1.2")

    // Rounded profile
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Lottie Animation
    val lottieVersion = "3.4.0"
    implementation("com.airbnb.android:lottie:$lottieVersion")

    // Shimmer Effect for layouts
    implementation("com.facebook.shimmer:shimmer:0.1.0")
    implementation("com.github.mikekpl:shimmer-recyclerview-x:2.0.0")

    // Swipe Refresh Layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Dexter for runtime permissions
    implementation("com.karumi:dexter:6.2.3")

    // Google Auth Library
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    implementation ("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")
}

// Apply the Google Services plugin
apply(plugin = "com.google.gms.google-services")
