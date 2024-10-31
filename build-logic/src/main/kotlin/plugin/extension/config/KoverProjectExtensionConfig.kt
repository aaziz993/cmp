package plugin.extension.config

import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project

internal fun Project.configKoverProjectExtension(extension: KoverProjectExtension) =
    extension.apply {
        reports {
            total {
                html {
                    title.set("Tests using Kover with Gradle")
                    htmlDir.set(layout.projectDirectory.file("build/reports/kover").asFile)
                    onCheck.set(true)
                }
                xml {
                    title.set("Tests using Kover with Gradle")
                    xmlFile.set(layout.projectDirectory.file("build/reports/kover/report.xml").asFile)
                    onCheck.set(true)
                }
            }
            filters {
                includes {
                    providers.gradleProperty("kover.filters.include.classes").get().let { c ->
                        if (c.isNotEmpty()) {
                            classes(
                                c.split(",").map { it.trim() },
                            )
                        }
                    }
                    providers.gradleProperty("kover.filters.include.packages").get().let { p ->
                        if (p.isNotEmpty()) {
                            packages(
                                p.split(",").map { it.trim() },
                            )
                        }
                    }
                }
                excludes {
                    providers.gradleProperty("kover.filters.exclude.classes").get().let { c ->
                        if (c.isNotEmpty()) {
                            classes(
                                c.split(",").map { it.trim() },
                            )
                        }
                    }
                    providers.gradleProperty("kover.filters.exclude.packages").get().let { p ->
                        if (p.isNotEmpty()) {
                            packages(
                                p.split(",").map { it.trim() },
                            )
                        }
                    }
                }
            }

            verify {
                rule {
                    disabled.set(providers.gradleProperty("kover.verify.rule.disabled").get().toBoolean())
                    bound {
                        minValue.set(providers.gradleProperty("kover.verify.rule.min.value").orNull?.toInt())
                        maxValue.set(providers.gradleProperty("kover.verify.rule.max.value").orNull?.toInt())
                        coverageUnits.set(
                            providers.gradleProperty("kover.verify.rule.coverage-unit").orNull?.let {
                                CoverageUnit.valueOf(it.uppercase())
                            } ?: CoverageUnit.LINE,
                        )
                    }
                }
            }
        }
    }
