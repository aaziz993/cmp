package ai.tech.core.misc.auth.server.jwt

import ai.tech.core.misc.type.accessor
import ai.tech.core.misc.type.callOrNull
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload

public fun <T> Payload.getClaim(
    vararg keys: String,
): T =
    claims?.let {
        if (keys.size == 1) {
            it[keys[0]]
        }
        else {
            it[keys.first()]?.callOrNull(keys.drop(1)) { _, _, v ->
                (v as Claim?)?.asMap()?.accessor()
            }
        }
    } as T

public inline fun <reified T> Claim.asList(): MutableList<T> = asList(T::class.java)

public inline fun <reified T> Claim.asArray(): Array<T> = asArray(T::class.java)
