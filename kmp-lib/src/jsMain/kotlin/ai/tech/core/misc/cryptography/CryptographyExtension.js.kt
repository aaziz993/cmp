package ai.tech.core.misc.cryptography

import ai.tech.core.data.model.Compression
import ai.tech.core.misc.cryptography.model.Curve
import ai.tech.core.misc.cryptography.model.ECC
import ai.tech.core.misc.cryptography.model.HashAlgorithm
import ai.tech.core.misc.cryptography.model.PGPKey
import ai.tech.core.misc.cryptography.model.PGPKeyMetadata
import ai.tech.core.misc.cryptography.model.PGPSignMode
import ai.tech.core.misc.cryptography.model.PGPSubKeyType
import ai.tech.core.misc.cryptography.model.PGPUserId
import ai.tech.core.misc.cryptography.model.PGPVerification
import ai.tech.core.misc.cryptography.model.PGPVerifiedResult
import ai.tech.core.misc.cryptography.model.RSA
import ai.tech.core.misc.cryptography.model.SymmetricAlgorithm
import ai.tech.core.misc.type.Object
import ai.tech.core.misc.type.multiple.decode
import ai.tech.core.misc.type.multiple.encode
import js.date.Date
import js.promise.Promise
import js.promise.catch
import js.typedarrays.Uint8Array
import js.typedarrays.toUint8Array

public actual suspend fun pgpKeyPair(
    key: PGPKey,
    subKeys: List<PGPSubKeyType>,
    userIDs: List<PGPUserId>,
    expireDate: Long,
    password: String?,
    armored: Boolean,
): ByteArray {
    require(userIDs.isNotEmpty()) {
        "UserIDs are required for key generation"
    }

    val keyType: String
    var curve: String? = null
    var rsaBits: Double? = null

    when (key) {
        is ECC -> {
            keyType = "ecc"
            curve = curveMap[key.curve]!!
        }

        is RSA -> {
            require(key.size >= MIN_RSA_KEY_SIZE) {
                "RSA size should be at least $MIN_RSA_KEY_SIZE, got: ${key.size}"
            }

            keyType = "rsa"
            rsaBits = key.size.toDouble()
        }
    }

    return generateKey(
        Object {
            type = keyType
            curve?.let { this.curve = it }
            rsaBits?.let { rsaBits = it }
            subkeys = subKeys.map {
                Object<SubkeyOptions> {
                    when (it.key) {
                        is ECC -> {
                            type = "ecc"
                            curve = curveMap[it.key.curve]
                        }

                        is RSA -> {
                            type = "rsa"
                            rsaBits = it.key.size.toDouble()
                        }

                        null -> {
                            type = keyType
                            this.curve = curve
                            this.rsaBits = rsaBits
                        }
                    }
                    sign = it.sign
                }
            }.toTypedArray().ifEmpty { null }
            this.userIDs = userIDs.map { uid ->
                Object<UserID> {
                    name = uid.name
                    comment = uid.comment
                    email = uid.email
                }
            }.toTypedArray()
            keyExpirationTime = expireDate.toDouble()
            passphrase = password
            config = Object<Config> {
                key.compressionAlgorithms?.let {
                    require(it.size == 1) {
                        "Only one compression algorithm allowed"
                    }
                    preferredCompressionAlgorithm = compressionMap[it.single()]!!
                }
                key.hashAlgorithms?.let {
                    require(it.size == 1) {
                        "Only one symmetric algorithm allowed"
                    }
                    preferredHashAlgorithm = hashAlgorithmMap[it.single()]!!
                }
                key.symmetricKeyAlgorithms?.let {
                    require(it.size == 1) {
                        "Only one symmetric algorithm allowed"
                    }
                    preferredSymmetricAlgorithm = symmetricAlgorithmMap[it.single()]!!
                }
            }
            format = if (armored) "armored" else "binary"
        },
    ).then { if (armored) (it.privateKey as String).encode() else (it.privateKey as Uint8Array).toByteArray() }.await()
}

private fun ByteArray.readKey(): Promise<Key> = readKey(
    Object {
        if (isPGPArmored) {
            armoredKey = decode()
        }
        else {
            binaryKey = toUint8Array()
        }
    },
)

private fun ByteArray.readPrivateKey(): Promise<PrivateKey> = readPrivateKey(
    Object {
        if (isPGPArmored) {
            armoredKey = decode()
        }
        else {
            binaryKey = toUint8Array()
        }
    },
)

public actual suspend fun ByteArray.pgpChangeKeyPassword(
    oldPasswords: List<String>,
    password: String?,
    armored: Boolean,
): ByteArray {
    require(oldPasswords.size == 1) {
        "Only one of old password allowed"
    }

    return readPrivateKey().flatThen { k ->
        decryptKey(
            Object {
                privateKey = k
                passphrase = oldPasswords.toTypedArray()
            },
        )
    }.flatThen { dk ->
        if (password == null) {
            Promise.resolve(dk)
        }
        else {
            encryptKey(
                Object {
                    privateKey = dk
                    passphrase = arrayOf(password)
                },
            )
        }
    }.then {
        if (armored) it.armor().encode() else it.write().toByteArray()
    }.await()
}

public actual suspend fun ByteArray.pgpPublicKey(armored: Boolean): ByteArray = readKey().then { it.toPublic() }.then { if (armored) it.armor().encode() else it.write().toByteArray() }.await()

public actual suspend fun ByteArray.pgpPrivateKeys(armored: Boolean): List<ByteArray> = readPrivateKeys(
    Object {
        if (isPGPArmored) {
            armoredKeys = decode()
        }
        else {
            binaryKeys = toUint8Array()
        }
    },
).then { it.map { if (armored) it.armor().encode() else it.write().toByteArray() } }.await()

public actual suspend fun ByteArray.pgpPublicKeys(armored: Boolean): List<ByteArray> = readKeys(
    Object {
        if (isPGPArmored) {
            armoredKeys = decode()
        }
        else {
            binaryKeys = toUint8Array()
        }
    },
).then { it.map { if (armored) it.armor().encode() else it.write().toByteArray() } }.await()

public actual suspend fun ByteArray.pgpKeyMetadata(): PGPKeyMetadata = readKey().flatThen { k ->
    k.getExpirationTime().then {
        PGPKeyMetadata(
            fingerprint = k.getFingerprint(),
            userIDs = k.getUserIDs().map(PGPUserId::parse),
            createDate = k.getCreationTime().getTime().toLong(),
            expireDate = it?.let { if (it is Date) it.getTime().toLong() else -1 } ?: 0,
        )
    }
}.await()

public actual suspend fun ByteArray.pgpKeyArmor(): ByteArray = readKey().then { it.armor().encode() }.await()

public actual suspend fun ByteArray.pgpKeyDearmor(): ByteArray = readKey().then { it.write().toByteArray() }.await()

public actual suspend fun ByteArray.pgpRevokeKey(
    vararg passwords: String,
    armored: Boolean,
): ByteArray = revokeKey(
    Object {
        key = readPrivateKey().await()
        format = if (armored) "armored" else "binary"
    },
).then { if (armored) (it as String).encode() else (it as Uint8Array).toByteArray() }.await()

private suspend fun List<ByteArray>.readDecrytedKeys(passwords: List<String>?): Array<PrivateKey> =

    map {
        it.readPrivateKey().flatThen {
            if (it.isDecrypted()) {
                Promise.resolve(it)
            }
            else {
                decryptKey(
                    Object {
                        privateKey = it
                        passphrase = passwords?.toTypedArray() ?: emptyArray<String>()
                    },
                )
            }
        }.await()
    }.toTypedArray()

public actual suspend fun ByteArray.pgpEncrypt(
    encryptionKeys: List<ByteArray>,
    signingKeys: List<ByteArray>?,
    signingKeysPasswords: List<String>?,
    passwords: List<String>?,
    armored: Boolean,
    isText: Boolean,
): ByteArray = ai.tech.core.misc.cryptography.encrypt(
    Object {
        message = createMessage(
            Object {
                if (isText) {
                    text = this@pgpEncrypt.decode()
                }
                else {
                    binary = this@pgpEncrypt
                }
            },
        ).await()
        this.encryptionKeys = encryptionKeys.map { it.readKey().await() }.toTypedArray()
        signingKeys?.let { this.signingKeys = signingKeys.readDecrytedKeys(signingKeysPasswords) }
        passwords?.let {
            this.passwords = it.toTypedArray()
        }
        format = if (armored) "armored" else "binary"
    },
).then { if (armored) (it as String).encode() else (it as Uint8Array).toByteArray() }.await()

public actual suspend fun ByteArray.pgpDecrypt(
    decryptionKeys: List<ByteArray>,
    decryptionKeysPasswords: List<String>?,
    verificationKeys: List<ByteArray>?,
    passwords: List<String>?,
): PGPVerifiedResult = readMessage(
    Object {
        if (isPGPArmored) {
            armoredMessage = this@pgpDecrypt.decode()
        }
        else {
            binaryMessage = this@pgpDecrypt
        }
    },
).await().let {
    ai.tech.core.misc.cryptography.decrypt(
        Object {
            this.decryptionKeys = decryptionKeys.readDecrytedKeys(decryptionKeysPasswords)
            verificationKeys?.let {
                this.verificationKeys = verificationKeys.map { it.readKey().await() }.toTypedArray()
            }
            passwords?.let { this.passwords = it.toTypedArray() }
        },
    ).await().let { dr ->
        dr.signatures.map { PGPVerification(it.keyID.toHex(), it.verified.catch { false }.await()) }.let {
            PGPVerifiedResult(
                (dr.data as String).encode(),
                { it },
            )
        }
    }
}

public actual suspend fun ByteArray.pgpSign(
    signingKeys: List<ByteArray>,
    signingKeysPasswords: List<String>?,
    mode: PGPSignMode,
    detached: Boolean,
    armored: Boolean,
): ByteArray = sign(
    Object {
        message = when (mode) {
            PGPSignMode.BINARY -> createMessage(
                Object {
                    binary = this@pgpSign
                },
            )

            PGPSignMode.TEXT -> createMessage(
                Object {
                    text = this@pgpSign.decode()
                },
            )

            PGPSignMode.CLEARTEXT_SIGN -> createCleartextMessage(
                Object {
                    text = decode()
                },
            )
        }.await()
        this.signingKeys = signingKeys.readDecrytedKeys(signingKeysPasswords)
        this.detached = detached
        format = if (armored) "armored" else "binary"
    },
).then { if (armored) (it as String).encode() else (it as Uint8Array).toByteArray() }.await()

public actual suspend fun ByteArray.pgpVerify(
    verificationKeys: List<ByteArray>,
    mode: PGPSignMode,
    signatures: List<ByteArray>?,
): PGPVerifiedResult {
    require(signatures == null || signatures.size == 1) {
        "Only one or zero signatures can be used to verify"
    }

    return verify(
        Object {
            message = when (mode) {
                PGPSignMode.BINARY -> createMessage(
                    Object {
                        binary = this@pgpVerify
                    },
                )

                PGPSignMode.TEXT -> createMessage(
                    Object {
                        text = decode()
                    },
                )

                PGPSignMode.CLEARTEXT_SIGN -> createCleartextMessage(
                    Object {
                        text = decode()
                    },
                )
            }.await()
            signatures?.first()?.let { s ->
                signature = readSignature(
                    Object {
                        if (s.isPGPArmored) {
                            armoredSignature = s.decode()
                        }
                        else {
                            binarySignature = s.toUint8Array()
                        }
                    },
                ).await()
            }
            this.verificationKeys = verificationKeys.map { it.readKey().await() }.toTypedArray()
        },
    ).await().let { vr ->
        vr.signatures.map { PGPVerification(it.keyID.toHex(), it.verified.catch { false }.await()) }.let {
            PGPVerifiedResult(
                if (mode == PGPSignMode.CLEARTEXT_SIGN) (vr.data as String).encode() else (vr.data as Uint8Array).toByteArray(),
                { it },
            )
        }
    }
}

private val curveMap = mapOf(
    Curve.CURVE25519 to "curve25519",
    Curve.ED25519 to "ed25519",
    Curve.P256 to "p256",
    Curve.P384 to "p384",
    Curve.P521 to "p521",
    Curve.P521 to "p521",
    Curve.BRAINPOOLP256R1 to "brainpoolP256r1",
    Curve.BRAINPOOLP384R1 to "brainpoolP384r1",
    Curve.BRAINPOOLP512R1 to "brainpoolP512r1",
    Curve.SECP256K1 to "secp256k1",
)

private val compressionMap = mapOf(
    Compression.UNCOMPRESSED to 0,
    Compression.ZIP to 1,
    Compression.ZLIB to 2,
    Compression.BZIP2 to 3,
)

private val hashAlgorithmMap = mapOf(
    HashAlgorithm.MD5 to 1,
    HashAlgorithm.SHA1 to 2,
    HashAlgorithm.RIPEMD to 3,
    HashAlgorithm.SHA256 to 8,
    HashAlgorithm.SHA384 to 9,
    HashAlgorithm.SHA512 to 10,
    HashAlgorithm.SHA224 to 11,
)

private val symmetricAlgorithmMap = mapOf(
    SymmetricAlgorithm.PLAINTEXT to 0,
    SymmetricAlgorithm.TRIPLEDES to 2,
    SymmetricAlgorithm.CAST_5 to 3,
    SymmetricAlgorithm.BLOWFISH to 4,
    SymmetricAlgorithm.AES_128 to 7,
    SymmetricAlgorithm.AES_192 to 8,
    SymmetricAlgorithm.AES_256 to 9,
    SymmetricAlgorithm.TWOFISH to 10,
)
