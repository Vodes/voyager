import com.android.build.gradle.BaseExtension
import extensions.isAndroidApplicationModule
import extensions.isAndroidLibraryModule
import extensions.isMultiplatformModule

configure<BaseExtension> {
    compileSdkVersion(34)
    defaultConfig {
        minSdk = 21

        if (isAndroidApplicationModule()) {
            targetSdk = 34
            versionCode = 1
            versionName = "1.0"
        }

        if (isAndroidLibraryModule()) {
            val proguardFilename = "consumer-rules.pro"
            if (layout.projectDirectory.file(proguardFilename).asFile.exists()) {
                consumerProguardFile(proguardFilename)
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        unitTests.all(Test::useJUnitPlatform)
    }

    if (isMultiplatformModule()) {
        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}
