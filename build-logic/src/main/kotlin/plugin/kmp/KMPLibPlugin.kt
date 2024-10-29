package plugin.kmp

import extension.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import plugin.BaseLibPlugin

public class KMPLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        KMPPlugin(
            target.id("android.library")
        ).apply(target).also {
            BaseLibPlugin().apply(target)
        }.also {
            with(target) {
//                extensions.configure<LibraryExtension>(::configComposeAndroidLibExtension)
            }
        }
}
