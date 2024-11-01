@file:JsModule("encoding-japanese")

package ai.tech.core.misc.type.multiple

import kotlinx.js.JsPlainObject

@JsPlainObject
internal external interface ConvertOptions {
    var to: String
    var type: String
}

internal external fun convert(
    data: Any,
    options: ConvertOptions,
): Any
