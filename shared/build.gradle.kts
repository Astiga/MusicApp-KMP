import org.jetbrains.kotlin.konan.target.Family

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

//    macosX64 {
//        binaries {
//            executable {
//                entryPoint = "main"
//            }
//        }
//    }
//    macosArm64 {
//        binaries {
//            executable {
//                entryPoint = "main"
//            }
//        }
//    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).filter { it.konanTarget.family == Family.IOS }
        .forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "shared"
                isStatic = true
                with(libs) {
                    export(bundles.decompose)
                    export(essenty.lifecycle)
                }
            }
        }
/*
    jvm("desktop")
    js(IR) {
        browser()
    }*/

    applyDefaultHierarchyTemplate()

    /*   cocoapods {
           summary = "Some description for the Shared Module"
           homepage = "Link to the Shared Module homepage"
           version = "1.0"
           ios.deploymentTarget = "14.1"
           podfile = project.file("../iosApp/Podfile")
       }*/

    sourceSets {
//        val desktopMain by getting

        commonMain.dependencies {
            implementation(libs.koin.core)

            with(compose) {
                implementation(ui)
                implementation(foundation)
                implementation(material3)
                implementation(runtime)
                implementation(components.resources)
            }

            with(libs) {
                implementation(kotlinx.serialization.json)
                implementation(ktor.client.core)
                implementation(ktor.logging)
                implementation(ktor.serialization)
                implementation(ktor.content.negotiation)
                api(bundles.decompose)
                implementation(image.loader)
                implementation(essenty.lifecycle)
                implementation(datastore.core)
                implementation(datastore.preferences)
            }
        }

        commonTest.dependencies {
            implementation(libs.bundles.testing)
            implementation(libs.kotlin.test.junit)
        }

        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.androidx.media3.exoplayer)
            }
        }

        androidUnitTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.mockk)
        }

        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:${libs.versions.ktor.get()}")
        }

        iosTest.dependencies {
            implementation(libs.kotlin.test)
        }

/*        desktopMain.dependencies {
            implementation(compose.desktop.common)
            implementation(libs.vlcj)
        }

        val desktopTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.mockk)
            }
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            with(libs) {
                implementation(ktor.client.js)
            }
        }

        jsTest.dependencies {
            implementation(libs.kotlin.test)
        }*/
    }
}

android {
    namespace = "com.example.musicapp_kmp"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
