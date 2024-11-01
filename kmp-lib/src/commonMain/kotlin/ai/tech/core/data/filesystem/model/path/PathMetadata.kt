package ai.tech.core.data.filesystem.model.path

import ai.tech.core.data.filesystem.fileSystem
import kotlin.reflect.KClass
import okio.Path.Companion.toPath

public data class PathMetadata(
    public val path: String,
    public val type: PathType,
    public val createdTime: Long?,
    public val lastAccessedTime: Long?,
    public val lastModifiedTime: Long?,
    public val size: Long?,
    public val extras: Map<KClass<*>, Any>,
) {
    public val isRegularFile: Boolean
        get() = type == PathType.REGULAR_FILE

    public val isDirectory: Boolean
        get() = type == PathType.DIRECTORY

    public val isSymbolicLink: Boolean
        get() = type == PathType.SYMBOLIC_LINK

    public val isOther: Boolean
        get() = type == PathType.OTHER

    public companion object {
        public operator fun invoke(pathString: String): PathMetadata =
            pathString.toPath().let { path ->
                fileSystem.metadata(path).let {
                    PathMetadata(
                        pathString,
                        if (it.isRegularFile) {
                            PathType.REGULAR_FILE
                        } else if (it.isDirectory) {
                            PathType.DIRECTORY
                        } else if (it.symlinkTarget != null) {
                            PathType.SYMBOLIC_LINK
                        } else {
                            PathType.OTHER
                        },
                        it.createdAtMillis,
                        it.lastAccessedAtMillis,
                        it.lastModifiedAtMillis,
                        it.size,
                        it.extras,
                    )
                }
            }
    }
}
