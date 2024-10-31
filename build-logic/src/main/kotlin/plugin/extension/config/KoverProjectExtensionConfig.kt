package plugin.extension.config

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project

internal fun Project.configKoverProjectExtension(extension: KoverProjectExtension) =
    extension.apply {
        reports.apply {
            filters.apply {
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

            verify.apply {
//                rule {
//                    isEnabled = providers.gradleProperty("kover.verify.rule.min.value").get().toBoolean()
//                    bound {
//                        minValue = providers.gradleProperty("kover.verify.rule.min.value").orNull?.toInt()
//                        maxValue = providers.gradleProperty("kover.verify.rule.max.value").orNull?.toInt()
//                        metric = providers.gradleProperty("kover.verify.rule.metric").orNull?.let {
//                            MetricType.valueOf(it.uppercase())
//                        } ?: MetricType.LINE
//                        aggregation = providers.gradleProperty("kover.verify.rule.aggregation").orNull?.let {
//                            AggregationType.valueOf(it.uppercase())
//                        } ?: AggregationType.COVERED_PERCENTAGE
//                    }
//                }
            }
        }
    }
