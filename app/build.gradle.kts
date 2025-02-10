import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.sibi.helpi"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.sibi.helpi"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load secrets from the properties file
        val properties = Properties()
        file("../secrets.properties").inputStream().use { properties.load(it) }
        val mapsApiKey = properties["MAPS_API_KEY"] as String
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")

        // Pass the MAPS_API_KEY to the manifest placeholders
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
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

    testOptions {
        packagingOptions {
            jniLibs {
                useLegacyPackaging = true
            }
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation("com.google.android.material:material:1.10.0")
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.coordinatorlayout)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.location)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.uiautomator)
    testImplementation(libs.junit)

    // for firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)

    // Maps SDK for Android
    implementation(libs.play.services.maps)
    implementation(libs.places)

    implementation(libs.material)
    implementation(libs.material.v190)

    // navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    // for image crop
    implementation(libs.hdodenhof.circleimageview)
    implementation(libs.ucrop)

    // for testing
    androidTestImplementation(libs.ext.junit)

    // for UI test
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.core.v351)
    androidTestImplementation(libs.junit.v115)
    androidTestImplementation(libs.rules)

    // for mockito
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.testng)
    testImplementation(libs.mockito.core)
}
