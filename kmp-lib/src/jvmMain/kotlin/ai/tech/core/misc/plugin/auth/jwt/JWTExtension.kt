package ai.tech.core.misc.plugin.auth.jwt

import ai.tech.core.misc.type.accessor
import ai.tech.core.misc.type.get
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import kotlin.reflect.KClass
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime

@Suppress("UNCHECKED_CAST")
public fun <T> Payload.getClaim(
    vararg keys: String,
): T = claims?.get(keys.toList()) { _, _, v -> (v as Claim?)?.let { it.asMap() ?: it.asList<Any?>() }?.accessor() } as T

public inline fun <reified T> Claim.asList(): List<T?>? = asList(T::class.java)

public inline fun <reified T> Claim.asArray(): Array<out T?>? = asArray(T::class.java)

public fun Claim.asKotlinLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime? = asDate()?.toInstant()?.toKotlinInstant()?.toLocalDateTime(timeZone)
