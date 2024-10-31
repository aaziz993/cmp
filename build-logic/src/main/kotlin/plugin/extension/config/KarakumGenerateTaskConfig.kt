package plugin.extension.config

import io.github.sgrishchenko.karakum.gradle.plugin.tasks.KarakumGenerate
import org.gradle.api.Project

internal val jsTypeImports =
    mapOf(
        "Date" to "js.date.Date",
        "ArrayBuffer" to "js.buffer.ArrayBuffer",
        "Promise" to "js.promise.Promise",
        "Uint8Array" to "js.typedarrays.Uint8Array",
        "ReadableStream" to "web.streams.ReadableStream",
    )

internal fun Project.configureKarakumGenerateTask(task: KarakumGenerate) =
    task.apply {
        doLast {
            val imports = mutableListOf<String>()

            val karakumConfigFile = file("karakum.config.json")

            val generatedDir =
                files(""""output": "(.*?)",""".toRegex().find(karakumConfigFile.readText())!!.groupValues[1])

            // Add internal modifier to all generated declarations
            generatedDir.asFileTree.forEach { file ->
                if (file.extension == "kt") {
                    val content = file.readText()

                    jsTypeImports.entries
                        .filter { (k, _) ->
                            content.contains("""(:|=)$k(\?|<|$)""".toRegex())
                        }.map { """"${it.value}"""" }
                        .let {
                            if (it.isNotEmpty()) {
                                imports.add(
                                    """
                            "${file.path.replaceFirst("${generatedDir.asPath}\\", "").replace("\\", "/")}" : [
                            ${it.joinToString(",\n")}
                            ]""",
                                )
                            }
                        }

                    // Use a regex to find and replace missing visibility modifiers
                    val modifiedContent =
                        content.replace(
                            Regex("""(?<=^)((sealed\s*)?external|typealias)(?=\s+)"""),
                            "internal $1",
                        )
                    file.writeText(modifiedContent)
                }
            }

            if (imports.isNotEmpty()) {
                karakumConfigFile.writeText(
                    karakumConfigFile.readText().replace(
                        """"importInjector"[\s\S]*?:[\s\S]*?\{[\s\S]*?\}""".toRegex(),
                        """
                        "importInjector":{
                           ${imports.joinToString(",\n")}
                        }
                        """.trimIndent(),
                    ),
                )
            }
        }
    }
