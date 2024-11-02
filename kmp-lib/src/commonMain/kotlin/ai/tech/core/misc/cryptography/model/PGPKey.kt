package ai.tech.core.misc.cryptography.model

import ai.tech.core.data.model.Compression

public sealed class PGPKey(
    public val compressionAlgorithms: List<Compression>?,
    public val hashAlgorithms: List<HashAlgorithm>?,
    public val symmetricKeyAlgorithms: List<SymmetricAlgorithm>?,
)

public class ECC(
    public val curve: Curve = Curve.CURVE25519,
    compressionAlgorithms: List<Compression>? = null,
    hashAlgorithms: List<HashAlgorithm>? = null,
    symmetricKeyAlgorithms: List<SymmetricAlgorithm>? = null,
) : PGPKey(compressionAlgorithms, hashAlgorithms, symmetricKeyAlgorithms)

public class RSA(
    public val size: Int = 4096,
    compressionAlgorithms: List<Compression>? = null,
    hashAlgorithms: List<HashAlgorithm>? = null,
    symmetricKeyAlgorithms: List<SymmetricAlgorithm>? = null,
) : PGPKey(compressionAlgorithms, hashAlgorithms, symmetricKeyAlgorithms)
