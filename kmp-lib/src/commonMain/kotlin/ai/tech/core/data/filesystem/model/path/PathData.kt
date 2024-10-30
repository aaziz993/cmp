package ai.tech.core.data.filesystem.model.path

import ai.tech.core.misc.type.multiple.model.AsyncIterator

public data class PathData(
    val path: String,
    val data: AsyncIterator<ByteArray>,
)
