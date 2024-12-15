import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import extensions.isMultiplatformModule
import extensions.kotlinMultiplatform

pluginManager.apply("com.vanniktech.maven.publish")
pluginManager.apply("maven-publish")

if (isMultiplatformModule()) {
    kotlinMultiplatform {
        androidTarget {
            publishLibraryVariants("release")
        }
    }
}

group = "moe.styx.forks.voyager"


configure<MavenPublishBaseExtension> {
    pom {
        description = "A pragmatic navigation library for Jetpack Compose"
        inceptionYear = "2021"

        licenses {
            license {
                name = "The MIT License"
                url = "https://opensource.org/licenses/MIT"
                distribution = "repo"
            }
        }
    }
}

configure<PublishingExtension> {
    repositories {
        maven {
            name = "Styx"
            url = if (version.toString().contains("-SNAPSHOT", true))
                uri("https://repo.styx.moe/snapshots")
            else
                uri("https://repo.styx.moe/releases")
            credentials {
                username = System.getenv("STYX_REPO_TOKEN")
                password = System.getenv("STYX_REPO_SECRET")
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
