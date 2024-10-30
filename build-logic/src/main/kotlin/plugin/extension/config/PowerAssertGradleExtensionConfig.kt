package plugin.extension.config

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.powerassert.gradle.PowerAssertGradleExtension

@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal fun Project.configPowerAssertGradleExtension(extension: PowerAssertGradleExtension): PowerAssertGradleExtension =
    extension.apply {
        functions.addAll(
            "kotlin.assert",
            "kotlin.test.assertTrue",
            "kotlin.test.assertEquals",
            "kotlin.test.assertNull"
        )
        includedSourceSets.addAll("commonMain", "jvmMain", "androidMain", "jsMain", "nativeMain")
    }