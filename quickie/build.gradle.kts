plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.dokka)
    `maven-publish`
    signing
}

android {
    namespace = "io.github.g00fy2.quickie"
    resourcePrefix = "quickie"
    buildFeatures {
        viewBinding = true
    }
    flavorDimensions += "mlkit"
    productFlavors {
        create("bundled").dimension = "mlkit"
        create("unbundled").dimension = "mlkit"
    }
    sourceSets {
        getByName("bundled").java.srcDirs("src/bundled/kotlin")
        getByName("unbundled").java.srcDirs("src/unbundled/kotlin")
    }
    publishing {
        singleVariant("bundledRelease") {
            withJavadocJar()
            withSourcesJar()
        }

        singleVariant("unbundledRelease") {
            withJavadocJar()
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)

    implementation(libs.androidx.camera)
    implementation(libs.androidx.cameraLifecycle)
    implementation(libs.androidx.cameraPreview)

    add("bundledImplementation", libs.mlkit.barcodeScanning)
    add("unbundledImplementation", libs.mlkit.barcodeScanningGms)

    testImplementation(libs.test.junit)
    testRuntimeOnly(libs.test.junit.platformLauncher)
}

//tasks.register<Jar>("androidJavadocJar") {
//    archiveClassifier = "javadoc"
//    from(layout.buildDirectory.dir("dokka/javadoc"))
//    dependsOn("dokkaJavadoc")
//}
//
//tasks.register<Jar>("androidBundledSourcesJar") {
//    archiveClassifier = "sources"
//    from(android.sourceSets.getByName("main").java.srcDirs, android.sourceSets.getByName("bundled").java.srcDirs)
//}
//
//tasks.register<Jar>("androidUnbundledSourcesJar") {
//    archiveClassifier = "sources"
//    from(android.sourceSets.getByName("main").java.srcDirs, android.sourceSets.getByName("unbundled").java.srcDirs)
//}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("bundledRelease") {
                commonConfig("bundled")
            }

            create<MavenPublication>("unbundledRelease") {
                commonConfig("unbundled")
            }
        }
    }
}

fun MavenPublication.commonConfig(flavor: String) {
    from(components["${flavor}Release"])
    groupId = "dk.makeable"
    artifactId = "code-scanner-android-$flavor"
    version = version
//    artifact(tasks.named("androidJavadocJar"))
//    artifact(tasks.named("android${flavor.replaceFirstChar { it.titlecase() }}SourcesJar"))
}