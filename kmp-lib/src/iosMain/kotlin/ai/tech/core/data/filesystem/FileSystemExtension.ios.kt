package ai.tech.core.data.filesystem

import platform.Foundation.NSURL

public actual val String.isValidFileUrl: Boolean
    get() = try {
        val url = NSURL.URLWithString(this)
        url != null && url.scheme == "file" && url.path != null
    }
    catch (e: Exception) {
        false
    }
