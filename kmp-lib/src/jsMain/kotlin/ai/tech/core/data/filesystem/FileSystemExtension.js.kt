package ai.tech.core.data.filesystem

import ai.tech.core.misc.platform.IS_NODE
import okio.FileSystem
import okio.NodeJsFileSystem
import web.url.URL

public actual val String.isValidFileUrl: Boolean
    get() = try {
        val uri = URL(this) // Use appropriate JS URI parsing method
        uri.protocol == "file:"
    }
    catch (e: Exception) {
        false
    }

internal actual val fileSystem: FileSystem
    get() = if (IS_NODE) {
        NodeJsFileSystem
    }
    else {
        throw UnsupportedOperationException("FileSystem not supported in browser for security reasons. Use it only on nodejs.")
    }
