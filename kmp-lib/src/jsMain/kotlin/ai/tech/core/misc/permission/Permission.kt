package ai.tech.core.misc.permission

import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
public external interface Permissions {
    public val permissions: Array<String>
}

public fun permissions(): Promise<Permissions> = js("browser.permissions.getAll()") as Promise<Permissions>
