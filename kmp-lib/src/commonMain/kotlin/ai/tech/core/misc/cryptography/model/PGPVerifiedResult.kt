package ai.tech.core.misc.cryptography.model

public data class PGPVerifiedResult(
    val data: ByteArray,
    val verifications: () -> List<PGPVerification>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PGPVerifiedResult

        if (!data.contentEquals(other.data)) return false
        if (verifications != other.verifications) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + verifications.hashCode()
        return result
    }
}