// Top-level build file where you can add configuration options common to all sub-projects/modules.
val secretsFile = file("secrets.properties")
extra.apply {
    if (secretsFile.exists()) {
        val properties = java.util.Properties()
        properties.load(secretsFile.inputStream())
        properties.forEach { (key, value) ->
            set(key.toString(), value)
        }
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}