@file:Suppress("ktlint:standard:no-wildcard-imports")

package ai.tech.core.data.filesystem

import ai.tech.core.data.filesystem.model.path.PathMetadata
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

public val filePR: Regex =
    "^file://.*".toRegex(
        RegexOption.IGNORE_CASE,
    )

public val String.isFileUrl: Boolean
    get() = matches(filePR)

public expect val String.isValidFileUrl: Boolean

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

public fun Iterator<PathMetadata>.traverser(
    depth: Int = 0,
    followSymlinks: Boolean = false,
    depthFirst: Boolean = false,
): Iterator<PathMetadata> {
    val trasform: Iterator<PathMetadata>.(Int, PathMetadata) -> Iterator<PathMetadata>? = { i, v ->
        if ((depth == -1 || i < depth) && (v.isDirectory || (v.isSymbolicLink && followSymlinks))) {
            v.path.fsPathIterator()
        }
        else {
            null
        }
    }

    return if (depthFirst) {
        depthIterator(trasform)
    }
    else {
        breadthIterator(trasform)
    }
}

internal expect val fileSystem: FileSystem

public val tmpDirPath: String = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.toString()

public val String.fsPathReal: String
    get() = fileSystem.canonicalize(toPath()).toString()

public val String.fsPathExists: Boolean
    get() = fileSystem.exists(toPath())

public val String.fsPathMetadata: PathMetadata
    get() = PathMetadata(this)

public val String.fsPathSymlink: String?
    get() = fileSystem.metadata(toPath()).symlinkTarget?.toString()

public val String.fsPathIsRelative: Boolean
    get() = toPath().isRelative

public val String.fsPathIsAbsolute: Boolean
    get() = toPath().isAbsolute

public val String.fsPathAbsolute: String
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

private fun String.fsPathIterator(): Iterator<PathMetadata> =
    fileSystem.list(toPath()).map { PathMetadata(it.toString()) }.iterator()

public fun String.fsPathTraverser(
    depth: Int = 0,
    followSymlinks: Boolean = false,
    depthFirst: Boolean = false,
): Iterator<PathMetadata> = fsPathIterator().traverser(depth, followSymlinks, depthFirst)

public fun String.fsPathCreateDirectory(ifNotExists: Boolean = false): Unit =
    fileSystem.createDirectories(toPath(), ifNotExists)

public fun String.fsPathCreateSymlink(target: String): Unit = fileSystem.createSymlink(toPath(), target.toPath())

public fun String.fsPathMove(target: String): Unit = fileSystem.atomicMove(toPath(), target.toPath())

public fun String.fsPathCopy(target: String): Unit = fileSystem.copy(toPath(), target.toPath())

public fun String.fsPathDelete(ifExists: Boolean = false): Unit = fileSystem.deleteRecursively(toPath(), ifExists)

public fun String.fsPathRead(bufferSize: Int = DEFAULT_BUFFER_SIZE): ClosableAbstractIterator<ByteArray> =
    fileSystem.source(toPath()).iterator(bufferSize)

public fun String.fsPathWrite(
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

public suspend fun String.fsPathWrite(
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
