package plugin.cmp.extension.config

import plugin.extension.settings
import plugin.extension.version
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

internal fun Project.configureDesktopExtension(extension: DesktopExtension): DesktopExtension =
    extension.apply {
        application {
            mainClass = "${settings.extension.projectGroup}.MainKt"

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

                linux {
                    iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
                }
                windows {
                    iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
                }
                macOS {
                    iconFile.set(project.file("desktopAppIcons/MacOSIcon.icns"))
                    bundleID = "${settings.extension.projectGroup}.${rootProject.name}.desktopApp"
                }
            }
            // also proguard rules
            buildTypes.release.proguard {
                configurationFiles.from("compose-desktop.pro")
            }
        }
    }
