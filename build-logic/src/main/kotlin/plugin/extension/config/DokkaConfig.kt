package plugin.extension.config

import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Delete
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.register
import org.jetbrains.dokka.gradle.DokkaTask

internal fun Project.configDokka() {
    val dokkaOutputDir = layout.buildDirectory.dir("dokka")
    tasks.withType(DokkaTask::class.java) { outputDirectory.set(file(dokkaOutputDir)) }

    val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
        delete(
            dokkaOutputDir,
        )
    }
    tasks.create<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependsOn(deleteDokkaOutputDir, "dokkaHtml")
        from(dokkaOutputDir)
    }
}
