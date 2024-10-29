package plugin.kmp

import extension.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import plugin.BaseLibPlugin

public class KMPLibPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        KMPPlugin(target.id("android.library")).apply(target)

        BaseLibPlugin().apply(target)
    }
}
