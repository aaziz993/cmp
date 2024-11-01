package ai.tech.core.data.filesystem

import okio.FileSystem

internal actual val fileSystem: FileSystem
    get() = FileSystem.SYSTEM

