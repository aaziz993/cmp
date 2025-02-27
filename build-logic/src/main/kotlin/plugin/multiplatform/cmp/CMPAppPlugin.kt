package plugin.multiplatform.cmp

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import plugin.multiplatform.extension.config.android.configureBaseAppModuleExtension
import plugin.extension.id
import plugin.multiplatform.cmp.extension.config.configureComposeAppExtension

public class CMPAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        CMPPlugin(target.id("android.application")).apply(target)

        with(target) {
            // Android app extension
            extensions.configure<BaseAppModuleExtension>(::configureBaseAppModuleExtension)

            extensions.configure<ComposeExtension>(::configureComposeAppExtension)
        }
    }
}
