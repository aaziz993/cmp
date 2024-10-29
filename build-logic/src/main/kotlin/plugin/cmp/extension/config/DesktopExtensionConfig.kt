package plugin.cmp.extension.config

import extension.settings
import extension.version
import org.gradle.api.Project
import org.gradle.kotlin.dsl.config
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

internal fun Project.configDesktopExtension(extension: DesktopExtension): DesktopExtension =
    extension.apply {
        application {
            mainClass = "${settings.config.group}.MainKt"

            jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED")
//            jvmArgs("--add-exports", "java.base/java.lang=ALL-UNNAMED")
//            jvmArgs("--add-exports", "java.desktop/sun.awt=ALL-UNNAMED")
//            jvmArgs("--add-exports", "java.desktop/sun.java2d=ALL-UNNAMED")
            if (System.getProperty("os.name").contains("Mac")) {
                jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
                jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
            }

            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = group.toString()
                packageVersion = version("desktop.package.version").toString()
            }
            // also proguard rules
            buildTypes.release.proguard {
                configurationFiles.from("compose-desktop.pro")
            }
        }

        settings.config.applyTo("jvm.app", this)
    }
