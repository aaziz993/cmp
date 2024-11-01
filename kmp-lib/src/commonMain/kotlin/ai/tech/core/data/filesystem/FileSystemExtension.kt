@file:Suppress("ktlint:standard:no-wildcard-imports")

package ai.tech.core.data.filesystem

import ai.tech.core.data.filesystem.model.path.PathMetadata
import ai.tech.core.misc.type.multiple.AP
import ai.tech.core.misc.type.multiple.LBP
import ai.tech.core.misc.type.multiple.breadthIterator
import ai.tech.core.misc.type.multiple.depthIterator
import ai.tech.core.misc.type.multiple.forEach
import ai.tech.core.misc.type.multiple.iterator
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import ai.tech.core.misc.type.multiple.model.ClosableAbstractIterator
import okio.Buffer
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.use

public expect fun getEnv(name: String): String?

public expect suspend fun String.toClipboard()

public expect suspend fun fromClipboard(): String?

public const val DEFAULT_BUFFER_SIZE: Int = 4096

public val stringExtensionRegexMap: Map<String, Regex> =
    mapOf(
        "json" to """^\s*(\{$AP*\}|\[$AP*\])\s*$""".toRegex(),
        "xml" to """^\s*<\?xml[\s\S]*""".toRegex(),
        "html" to """^\s*<(!DOCTYPE +)?html$AP*""".toRegex(),
        "yaml" to """^( *((#|[^{\s]*:|-).*)?$LBP?)+$""".toRegex(),
        "properties" to """^( *((#|[^{\s\[].*?=).*)?$LBP?)+$""".toRegex(),
        "toml" to """^( *(([#\[\]"{}]|.*=).*)?$LBP?)+$""".toRegex(),
    )

public val String.pathNormalized: String
    get() = toPath(true).toString()

public val String.pathSegments: List<String>
    get() = toPath(true).segments

public fun String.pathRelativeTo(other: String): String = toPath().relativeTo(other.toPath()).toString()

public fun String.pathResolve(child: String): String = toPath().resolve(child.toPath()).toString()

public fun String.pathResolveToRoot(
    fromRootPath: String,
    toRootPath: String,
): String = toRootPath.pathResolve(pathRelativeTo(fromRootPath))

public val String.pathIsRoot: Boolean
    get() = toPath().isRoot

public val String.pathRoot: String?
    get() = toPath().root?.toString()

public val String.pathParent: String?
    get() = toPath().parent?.toString()

public val String.pathExtension: String?
    get() = substringAfterLast(".", "").ifEmpty { null }

public val String.extension: String?
    get() = stringExtensionRegexMap.entries.find { (_, r) -> r.matches(this) }?.key

public fun Iterator<PathMetadata>.traverser(
    depth: Int = 0,
    followSymlinks: Boolean = false,
    depthFirst: Boolean = false,
): Iterator<PathMetadata> {
    val trasform: Iterator<PathMetadata>.(Int, PathMetadata) -> Iterator<PathMetadata>? = { i, v ->
        if ((depth == -1 || i < depth) && (v.isDirectory || (v.isSymbolicLink && followSymlinks))) {
            v.path.localPathIterator()
        } else {
            null
        }
    }

    return if (depthFirst) {
        depthIterator(trasform)
    } else {
        breadthIterator(trasform)
    }
}

internal expect val fileSystem: FileSystem

public val tmpDirPath: String = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.toString()

public val String.localPathReal: String
    get() = fileSystem.canonicalize(toPath()).toString()

public val String.localPathExists: Boolean
    get() = fileSystem.exists(toPath())

public val String.localPathMetadata: PathMetadata
    get() = PathMetadata(this)

public val String.localPathSymlink: String?
    get() = fileSystem.metadata(toPath()).symlinkTarget?.toString()

public val String.localPathIsRelative: Boolean
    get() = toPath().isRelative

public val String.localPathIsAbsolute: Boolean
    get() = toPath().isAbsolute

public val String.localPathAbsolute: String
    get() =
        toPath()
            .let {
                when {
                    it.isAbsolute -> this
                    else -> {
                        val currentDir = "".toPath()
                        fileSystem.canonicalize(currentDir) / (this)
                    }
                }
            }.toString()

private fun String.localPathIterator(): Iterator<PathMetadata> =
    fileSystem.list(toPath()).map { PathMetadata(it.toString()) }.iterator()

public fun String.localPathTraverser(
    depth: Int = 0,
    followSymlinks: Boolean = false,
    depthFirst: Boolean = false,
): Iterator<PathMetadata> = localPathIterator().traverser(depth, followSymlinks, depthFirst)

public fun String.localPathCreateDirectory(ifNotExists: Boolean = false): Unit =
    fileSystem.createDirectories(toPath(), ifNotExists)

public fun String.localPathCreateSymlink(target: String): Unit = fileSystem.createSymlink(toPath(), target.toPath())

public fun String.localPathMove(target: String): Unit = fileSystem.atomicMove(toPath(), target.toPath())

public fun String.localPathCopy(target: String): Unit = fileSystem.copy(toPath(), target.toPath())

public fun String.localPathDelete(ifExists: Boolean = false): Unit = fileSystem.deleteRecursively(toPath(), ifExists)

public fun String.localPathRead(bufferSize: Int = DEFAULT_BUFFER_SIZE): ClosableAbstractIterator<ByteArray> =
    fileSystem.source(toPath()).iterator(bufferSize)

public fun String.localPathWrite(
    data: Iterator<ByteArray>,
    ifNotExists: Boolean = false,
): Unit =
    fileSystem.sink(toPath(), ifNotExists).use { sink ->
        val buffer = Buffer()
        data.forEach {
            sink.write(buffer.write(it), it.size.toLong())
            sink.flush()
            buffer.clear()
        }
    }

public suspend fun String.localPathWrite(
    data: AsyncIterator<ByteArray>,
    ifNotExists: Boolean = false,
): Unit =
    fileSystem.sink(toPath(), ifNotExists).use { sink ->
        val buffer = Buffer()
        data.forEach {
            sink.write(buffer.write(it), it.size.toLong())
            sink.flush()
            buffer.clear()
        }
    }
