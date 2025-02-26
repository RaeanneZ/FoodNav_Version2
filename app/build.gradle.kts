import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "sg.edu.np.mad.mad24p03team2"
    compileSdk = 34

    defaultConfig {
        applicationId = "sg.edu.np.mad.mad24p03team2"
        minSdk = 31
        targetSdk = 34
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load the API key from local.properties
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { properties.load(it) }
        }
        buildConfigField("String", "API_KEY", "\"${properties.getProperty("GPT_API_KEY")}\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true // Enable buildConfig feature
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.recyclerview)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0") // for recycler view
    testImplementation("junit:junit:4+")
    implementation("net.sourceforge.jtds:jtds:1.3.1") //net.sourceforge.jtds:jtds:1.3.1
    implementation("com.android.volley:volley:1.2.1") //com.android.volley:volley:1.2.1


    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.1") //com.fasterxml.jackson.core:jackson-databind:2.10.1
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.10.1") //com.fasterxml.jackson.core:jackson-databind:2.10.1


    implementation("com.squareup.okhttp3:okhttp:4.9.1") //com.squareup.okhttp3:okhttp:4.9.1
    implementation("com.google.code.gson:gson:2.10.1") //com.google.code.gson:gson:2.10.1
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0") // Update version as needed

    //library to take care of UI interactions
    //implementation("com.jakewharton.rxbinding:rxbinding:0.4.0")

    //for animation
    implementation("com.airbnb.android:lottie:6.4.1")

    //to encrypt info stored in shared preference
    implementation("androidx.security:security-crypto:1.0.0-alpha02")

    //MLKit Unbundled : Models are downloaded and managed via Google Play Services.
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")


    //implementation ("androidx.core:core-ktx:1.3.2")
    implementation ("com.google.android.material:material:1.2.1")
    implementation ("androidx.appcompat:appcompat:1.2.0")
    implementation ("androidx.core:core-ktx:1.6.0")
    implementation ("com.google.android.material:material:1.4.0")

    //text recognition for Korean and English characters
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-korean:16.0.0")
    implementation("com.google.mlkit:translate:17.0.2")
}
