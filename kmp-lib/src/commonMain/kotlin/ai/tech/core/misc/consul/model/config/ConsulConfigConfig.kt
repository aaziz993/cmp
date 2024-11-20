package ai.tech.core.misc.consul.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class ConsulConfigConfig(
    val prefix: String = "config",
    // defaults to the value of the application.name property
    val name: String?,
    val profileSeparator: String = ",",
    val defaultContext: String = "application",
    val dataKey: String? = null,
    val format: String = "yaml",
    val aclToken: String? = null,
    val failFast: Boolean = false,
) {

    public fun getKeys(applicationName: String, profiles: List<String> = emptyList()): List<String> {
        val dataKeyPart = dataKey.orEmpty()

        val namePart = name ?: applicationName

        return profiles.map { "$prefix/$namePart$profileSeparator$it/$dataKeyPart" } + listOf(
            "$prefix/$namePart/$dataKeyPart",
            "$prefix/$defaultContext$dataKeyPart",
        )
    }
}
