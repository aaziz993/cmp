package plugin.cmp

import plugin.extension.id
import org.gradle.api.Plugin
import org.gradle.api.Project
import plugin.BaseLibPlugin

public class CMPLibPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        CMPPlugin(target.id("android.library")).apply(target)

        BaseLibPlugin().apply(target)
    }
}
