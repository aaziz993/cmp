@file:Suppress("UnnecessaryVariable")

package core.processor

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import core.template.templates
import java.io.OutputStream
import kotlin.properties.Delegates

public class CompilerProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    private var reflectionsImports = mutableSetOf(
        "kotlin.reflect.KClass", "core.type.model.ObjectReflection", "core.type.model.AbstractReflection"
    )

    public operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {

        if (options["CREATE_REFLECTION_CLASSES"] == "true") {
            templates.forEach { t ->
                codeGenerator.getFile(t.key.first, t.key.second).let {
                    it += "package ${t.key.first}\n\n${t.value}"
                    it.close()
                }
            }
        }

        val reflectionsFile = codeGenerator.getFile("core.type", "Reflections")

        reflectionsFile += "package core.type\n\n"


        reflectionsFile += "${reflectionsImports.joinToString("\n") { "import $it" }}\n\n${
            objectReflections(resolver)
        }\n\n${
            sealedReflections(resolver)
        }\n\n${
            kClassReflections(resolver)
        }"

        reflectionsFile.close()

        return emptyList()
    }

    private fun objectReflections(resolver: Resolver): String =
        core.template.objectReflections(if (options["OBJECTS"] == "true") {
            resolver.getAllClasses().filter {
                it.classKind == ClassKind.OBJECT && it.isPublic()
            }.let {
                if (it.iterator().hasNext()) {
                    reflectionsImports += "kotlin.reflect.typeOf"
                }

                it.joinToString(",\n") {
                    reflectionsImports += it.qualifiedName!!.asString()

                    """    Object(
        ${it.qualifiedName!!.asString()}
        ${it.simpleName.getShortName()},
        ${it.annotations.toAnnotationsString()},
        ${it.superTypes.toTypeReferencesString()}
    )${"\n"}"""
                }
            }
        } else {
            ""
        })

    private fun sealedReflections(resolver: Resolver): String =
        core.template.sealedReflections(if (options["SEALEDS"] == "true") {
            resolver.getAllClasses().filter {
                Modifier.SEALED in it.modifiers && it.isPublic()
            }.joinToString(",\n") {
                reflectionsImports += it.qualifiedName!!.asString()

                "    ${it.simpleName.asString()}::class to listOf(\n${
                    it.getSealedSubclasses().joinToString(",\n") {
                        reflectionsImports += it.qualifiedName!!.asString()
                        "        ${it.simpleName.asString()}::class"
                    }
                }\n    )"
            }
        } else {
            ""
        })

    private val kClassReflections = mutableListOf<String>()

    private fun kClassReflections(resolver: Resolver): String {
        options["REFLECT_ANNOTATIONS"]?.let {
            it.split(",").map { it.trim() }.fold(emptySequence<KSClassDeclaration>()) { acc, v ->
                logger.info("Reflect classes with annotation \"$v\"")
                acc + resolver.getSymbolsWithAnnotation(v).filterIsInstance<KSClassDeclaration>()
            }.forEach { it.accept(ReflectVisitor(kClassReflections), Unit) }
        }

        if (kClassReflections.isNotEmpty()) {
            reflectionsImports += setOf(
                "kotlin.reflect.typeOf",
                "core.type.model.Member",
                "core.type.model.ReflectionImpl",
            )
        }

        return core.template.kClassReflections(kClassReflections.joinToString(",\n"))
    }

    private inner class ReflectVisitor(private val reflections: MutableList<String>) : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            reflectionsImports += classDeclaration.qualifiedName!!.asString()

            reflections += """    ReflectionImpl(
        ${classDeclaration.qualifiedName!!.asString()},
        ${classDeclaration.simpleName.asString()}::class,
        ${classDeclaration.annotations.toAnnotationsString()},
        ${classDeclaration.superTypes.toTypeReferencesString()},
            { ${
                if (classDeclaration.getConstructors()
                        .any { it.parameters.none { !it.type.resolve().isMarkedNullable } }
                ) {
                    "${classDeclaration.simpleName.asString()}()"
                } else {
                    "throw Exception(\"Constructor not found.\")"
                }
            } },
             listOf(${"\n"}${
                classDeclaration.declarations.filter { it.isPublic() && it.simpleName.asString() != "<init>" && it.validate() }
                    .toMembersString(classDeclaration) + (classDeclaration.declarations.filterIsInstance<KSClassDeclaration>()
                    .find { it.isCompanionObject }?.let {
                        it.declarations.filter { it.isPublic() && it.simpleName.asString() != "<init>" && it.validate() }
                            .toMembersString(it)
                    } ?: "")
            }${"\n"}        )
    )"""
        }
    }

    private fun CodeGenerator.getFile(packageName: String, fileName: String): OutputStream {
        return try {
            createNewFile(
                Dependencies.ALL_FILES, packageName, fileName
            )
        } catch (ex: FileAlreadyExistsException) {
            ex.file.outputStream()
        }
    }

    private fun Resolver.getAllClasses() = getAllFiles().flatMap {
        it.declarations.filterIsInstance<KSClassDeclaration>()
    }

    private val ignoreAnnotated = listOf("kotlin.OptionalExpectation")

    private fun Sequence<KSAnnotation>.toAnnotationsString() = """listOf(${
        filter {
            it.annotationType.resolve().declaration.annotations.none { it.annotationType.resolve().declaration.qualifiedName?.asString() in ignoreAnnotated }
        }.joinToString {
            reflectionsImports += it.annotationType.resolve().declaration.qualifiedName!!.asString()
            "${it.shortName.asString()}()"
        }
    })"""

    private fun Sequence<KSTypeReference>.toTypeReferencesString() = """listOf(${
        joinToString {
            it.resolve().let {
                reflectionsImports += it.declaration.qualifiedName!!.asString()

                it.toTypeOfString()
            }
        }
    })"""

    private fun Sequence<KSDeclaration>.toMembersString(classDeclaration: KSClassDeclaration) =
        joinToString(",\n") { m ->
            var isProperty by Delegates.notNull<Boolean>()
            lateinit var type: KSType
            var isStatic by Delegates.notNull<Boolean>()
            var isSuspend: Boolean? = null
            val isDeclared = m.parentDeclaration == classDeclaration
            var isExtension by Delegates.notNull<Boolean>()

            var isMutable by Delegates.notNull<Boolean>()

            when (m) {
                is KSPropertyDeclaration -> {
                    isProperty = true
                    type = m.type.resolve()
                    isStatic = Modifier.JAVA_STATIC in m.modifiers
                    isExtension = m.extensionReceiver != null
                    isMutable = m.isMutable
                }

                is KSFunctionDeclaration -> {
                    isProperty = false
                    type = m.returnType!!.resolve()
                    isStatic = m.functionKind == FunctionKind.STATIC
                    isSuspend = Modifier.SUSPEND in m.modifiers
                    isExtension = m.extensionReceiver != null
                    isMutable = true
                }

                else -> return@joinToString ""
            }

            reflectionsImports += type.declaration.qualifiedName!!.asString()
            """            Member(
                "${m.simpleName.asString()}",
                ${type.toTypeOfString()},
                $isStatic,
                $isSuspend,
                $isDeclared,
                $isExtension,
                ${classDeclaration.isCompanionObject},
                ${
                if (isSuspend == true) {
                    "{ _, _, _ -> throw UnsupportedOperationException() },\n                "
                } else {
                    ""
                }
            }{ arg, spread, parent -> ${"\n"}${
                if (isMutable) {
                    if (isStatic) {
                        "${classDeclaration.simpleName.asString()}.${m.simpleName.asString()}"
                    } else {
                        "(parent as ${classDeclaration.simpleName.asString()}).${m.simpleName.asString()}"
                    }.let {
                        if (isProperty) {
                            "                        $it = arg as ${type.toTypeString()}\n                        null"
                        } else {
                            it.withArgs((m as KSFunctionDeclaration).parameters)
                        }
                    }
                } else "                    throw UnsupportedOperationException()"
            }${"\n"}                },
                ${
                if (isSuspend != true) {
                    "{ _, _, _ -> throw UnsupportedOperationException() },\n                "
                } else {
                    ""
                }
            }${
                m.annotations.let {
                    if (it.iterator().hasNext()) {
                        it.toAnnotationsString()
                    } else {
                        "emptyList()"
                    }
                }
            }
            )"""
        }

    private fun KSType.toTypeString() = "${
        declaration.simpleName.asString()
    }${
        declaration.typeParameters.let {
            if (it.isEmpty()) {
                ""
            } else {
                "<${it.joinToString { "*" }}>"
            }
        }
    }${
        if (isMarkedNullable) {
            "?"
        } else {
            ""
        }
    }"

    private fun KSType.toTypeOfString() = "typeOf<${toTypeString()}>()"

    private fun String.withArgs(parameters: List<KSValueParameter>) = """                    if (spread) {
                        if (arg == null) {
                        ${
        if (parameters.isEmpty()) {
            "    return@Member $this()"
        } else {
            "    throw IllegalArgumentException(\"Function can't take zero arguments\")"
        }
    }
                        } else when (arg) {
                            is List<*> -> return@Member $this(${
        parameters.withIndex().joinToString(", ") { (i, p) -> "arg[$i] as ${p.type.resolve().toTypeString()}" }
    })
                            is Map<*, *> -> return@Member $this(${
        parameters.joinToString(", ") {
            "$it = arg[\"$it\"] as ${
                it.type.resolve().toTypeString()
            }"
        }
    })
                        }
                    }
    ${
        if (parameters.size == 1) {
            "                $this(arg) "
        } else {
            "                throw IllegalArgumentException(\"Function can't take one argument\")"
        }
    }"""
}
