package ai.tech.core.misc.platform

import ai.tech.core.misc.platform.model.JsBrowserPlatform
import ai.tech.core.misc.platform.model.JsNodePlatform
import ai.tech.core.misc.platform.model.Platform

@Suppress("MaxLineLength")
internal val IS_BROWSER: Boolean = js(
    "typeof window !== 'undefined' && typeof window.document !== 'undefined' || typeof self !== 'undefined' && typeof self.location !== 'undefined'"
) as Boolean

internal val IS_NODE: Boolean = js(
    "typeof process !== 'undefined' && process.versions != null && process.versions.node != null"
) as Boolean

public actual fun getPlatform(): Platform =
    when {
        IS_BROWSER -> JsBrowserPlatform("Web with Kotlin/Js Browser")
        IS_NODE -> JsNodePlatform("Web with Kotlin/Js Node")
        else -> throw UnsupportedOperationException("Unsupported JS runtime")
    }

