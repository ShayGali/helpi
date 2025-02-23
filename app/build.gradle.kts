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
        unitTests {
            isReturnDefaultValues = true
        }
        packagingOptions {
            jniLibs {
                useLegacyPackaging = true
            }
        }
        unitTests.all {
            it.jvmArgs(
                "--add-opens", "java.base/java.lang=ALL-UNNAMED",
                "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
                "--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED",
                "--add-opens", "java.base/java.util=ALL-UNNAMED",
                "--add-opens", "java.base/java.util.concurrent=ALL-UNNAMED"
            )
        }
    }
}


dependencies {
    // App dependencies
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

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)

    // Maps SDK
    implementation(libs.play.services.maps)
    implementation(libs.places)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Image loading
    implementation(libs.glide)
    implementation(libs.firebase.messaging)
    annotationProcessor(libs.compiler)

    // Image crop
    implementation(libs.hdodenhof.circleimageview)
    implementation(libs.ucrop)

    // Testing
    testImplementation("junit:junit:4.13.2") // Explicit JUnit 4
    androidTestImplementation(libs.ext.junit)

    // UI Testing
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.rules)

    // Mockito and PowerMock
    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
    testImplementation("androidx.arch.core:core-testing:2.2.0") // For LiveData testing
    testImplementation("net.java.dev.jna:jna:5.12.1") // Required for Mockito inline mocking on Java 17+
}