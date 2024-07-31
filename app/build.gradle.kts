plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.uecfs"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.uecfs"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.play.services.location)
    implementation(libs.glide)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.amitshekhar.android:android-networking:1.0.2")
    implementation ("io.noties.markwon:core:4.6.2")
    implementation ("com.google.code.gson:gson:2.8.8")
    implementation ("com.airbnb.android:lottie:6.4.0")
    implementation ("com.balsikandar.android:crashreporter:1.1.0")
    implementation ("com.github.unaisulhadi:emojiratingbar:1.0.5")
    implementation ("app.futured.donut:donut:2.2.3")
    implementation ("com.github.blackfizz:eazegraph:1.2.2@aar")
    implementation ("com.nineoldandroids:library:2.4.0")
    implementation ("com.github.douglasjunior:android-simple-tooltip:1.1.0")

}