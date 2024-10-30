package ai.tech.core.data.filesystem.model.path

import kotlin.reflect.KClass

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
}
