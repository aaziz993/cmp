package ai.tech.core.misc.cryptography.model

public data class PGPSubKeyType(
    public val key: PGPKey? = null,
    public val sign: Boolean = false,
)