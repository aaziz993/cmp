package ai.tech.core.misc.cryptography

import ai.tech.core.misc.cryptography.model.PGPKeyMetadata
import ai.tech.core.misc.cryptography.model.PGPKey
import ai.tech.core.misc.cryptography.model.PGPSignMode
import ai.tech.core.misc.cryptography.model.PGPSubKeyType
import ai.tech.core.misc.cryptography.model.PGPUserId
import ai.tech.core.misc.cryptography.model.PGPVerifiedResult

public actual suspend fun pgpKeyPair(
    key: PGPKey,
    subKeys: List<PGPSubKeyType>,
    userIDs: List<PGPUserId>,
    expireDate: Long,
    password: String?,
    armored: Boolean,
): ByteArray {
    TODO("Not yet implemented")
}

public actual suspend fun ByteArray.pgpChangeKeyPassword(
    oldPasswords: List<String>,
    password: String?,
    armored: Boolean,
): ByteArray {
    TODO("Not yet implemented")
}

public actual suspend fun ByteArray.pgpPublicKey(armored: Boolean): ByteArray {
    TODO("Not yet implemented")
}

public actual suspend fun ByteArray.pgpPrivateKeys(armored: Boolean): List<ByteArray> = TODO("Not yet implemented")

public actual suspend fun ByteArray.pgpPublicKeys(armored: Boolean): List<ByteArray> = TODO("Not yet implemented")

public actual suspend fun ByteArray.pgpKeyMetadata(): PGPKeyMetadata = TODO("Not yet implemented")

public actual suspend fun ByteArray.pgpKeyArmor(): ByteArray {
    TODO("Not yet implemented")
}

public actual suspend fun ByteArray.pgpKeyDearmor(): ByteArray {
    TODO("Not yet implemented")
}

public actual suspend fun ByteArray.pgpRevokeKey(
    vararg passwords: String,
    armored: Boolean,
): ByteArray {
    TODO("Not yet implemented")
}

public actual suspend fun ByteArray.pgpEncrypt(
    encryptionKeys: List<ByteArray>,
    signingKeys: List<ByteArray>?,
    signingKeysPasswords: List<String>?,
    passwords: List<String>?,
    armored: Boolean,
    isText: Boolean,
): ByteArray {
    TODO("Not yet implemented")
}

public actual suspend fun ByteArray.pgpDecrypt(
    decryptionKeys: List<ByteArray>,
    decryptionKeysPasswords: List<String>?,
    verificationKeys: List<ByteArray>?,
    passwords: List<String>?,
): PGPVerifiedResult {
    TODO("Not yet implemented")
}

public actual suspend fun ByteArray.pgpSign(
    signingKeys: List<ByteArray>,
    signingKeysPasswords: List<String>?,
    mode: PGPSignMode,
    detached:Boolean,
    armored: Boolean,
): ByteArray {
    TODO("Not yet implemented")
}

public actual suspend fun ByteArray.pgpVerify(
    verificationKeys: List<ByteArray>,
    mode: PGPSignMode,
    signatures: List<ByteArray>?
): PGPVerifiedResult {
    TODO("Not yet implemented")
}
