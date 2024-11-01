package ai.tech.core.data.filesystem

import java.net.URI

public actual fun getEnv(name: String): String? = System.getenv(name)

public actual val String.isValidFileUrl: Boolean
    get() = try {
        val uri = URI(this)
        uri.scheme == "file" && uri.isAbsolute && uri.path != null
    }
    catch (e: Exception) {
        false
    }
