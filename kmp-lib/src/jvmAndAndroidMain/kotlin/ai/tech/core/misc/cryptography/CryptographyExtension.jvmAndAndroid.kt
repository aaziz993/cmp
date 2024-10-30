package ai.tech.core.misc.cryptography

import ai.tech.core.misc.cryptography.model.CurveType
import ai.tech.core.misc.cryptography.model.ECC
import ai.tech.core.misc.cryptography.model.PGPKey
import ai.tech.core.misc.cryptography.model.PGPKeyMetadata
import ai.tech.core.misc.cryptography.model.PGPSignMode
import ai.tech.core.misc.cryptography.model.PGPSubKeyType
import ai.tech.core.misc.cryptography.model.PGPUserId
import ai.tech.core.misc.cryptography.model.PGPVerification
import ai.tech.core.misc.cryptography.model.PGPVerifiedResult
import ai.tech.core.misc.cryptography.model.RSA
import ai.tech.core.misc.cryptography.model.SymmetricAlgorithm
import ai.tech.core.data.model.Compression
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.openpgp.PGPException
import org.bouncycastle.openpgp.PGPKeyRing
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import org.bouncycastle.openpgp.PGPRuntimeOperationException
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection
import org.pgpainless.PGPainless
import org.pgpainless.algorithm.CompressionAlgorithm
import org.pgpainless.algorithm.HashAlgorithm
import org.pgpainless.algorithm.KeyFlag
import org.pgpainless.algorithm.SymmetricKeyAlgorithm
import org.pgpainless.decryption_verification.ConsumerOptions
import org.pgpainless.decryption_verification.SignatureVerification
import org.pgpainless.exception.MalformedOpenPgpMessageException
import org.pgpainless.exception.MissingDecryptionMethodException
import org.pgpainless.exception.WrongPassphraseException
import org.pgpainless.key.generation.KeySpec
import org.pgpainless.key.generation.KeySpecBuilder
import org.pgpainless.key.generation.type.KeyType
import org.pgpainless.key.generation.type.ecc.EllipticCurve
import org.pgpainless.key.generation.type.ecc.ecdh.ECDH
import org.pgpainless.key.generation.type.ecc.ecdsa.ECDSA
import org.pgpainless.key.generation.type.eddsa.EdDSACurve
import org.pgpainless.key.generation.type.rsa.RsaLength
import org.pgpainless.key.generation.type.xdh.XDHSpec
import org.pgpainless.key.info.KeyRingInfo
import org.pgpainless.key.util.UserId
import org.pgpainless.sop.MatchMakingSecretKeyRingProtector
import org.pgpainless.sop.SOPImpl
import org.pgpainless.util.ArmoredOutputStreamFactory
import org.pgpainless.util.Passphrase
import sop.SOP
import sop.enums.InlineSignAs
import sop.exception.SOPGPException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.InvalidAlgorithmParameterException
import java.security.NoSuchAlgorithmException
import java.util.*

private var sop: SOP = SOPImpl()

private fun InputStream.readSecretKeys(requireContent: Boolean): PGPSecretKeyRingCollection {
    val keys =
        try {
            PGPainless.readKeyRing().secretKeyRingCollection(this)
        } catch (e: IOException) {
            if (e.message == null) {
                throw e
            }
            if (e.message!!.startsWith("unknown object in stream:") ||
                e.message!!.startsWith("invalid header encountered")
            ) {
                throw SOPGPException.BadData(e)
            }
            throw e
        }
    if (requireContent && keys.none()) {
        throw SOPGPException.BadData(PGPException("No key data found."))
    }

    return keys
}

private fun InputStream.readPublicKeys(requireContent: Boolean): PGPPublicKeyRingCollection {
    val certs =
        try {
            PGPainless.readKeyRing().keyRingCollection(this, false)
        } catch (e: IOException) {
            if (e.message == null) {
                throw e
            }
            if (e.message!!.startsWith("unknown object in stream:") ||
                e.message!!.startsWith("invalid header encountered")
            ) {
                throw SOPGPException.BadData(e)
            }
            throw e
        } catch (e: PGPRuntimeOperationException) {
            throw SOPGPException.BadData(e)
        }

    if (certs.pgpSecretKeyRingCollection.any()) {
        throw SOPGPException.BadData(
            "Secret key components encountered, while certificates were expected.",
        )
    }

    if (requireContent && certs.pgpPublicKeyRingCollection.none()) {
        throw SOPGPException.BadData(PGPException("No cert data found."))
    }
    return certs.pgpPublicKeyRingCollection
}

private fun Compression.toCompressionAlgorithm(): CompressionAlgorithm =
    when (this) {
        Compression.UNCOMPRESSED -> CompressionAlgorithm.UNCOMPRESSED
        Compression.ZIP -> CompressionAlgorithm.ZIP
        Compression.ZLIB -> CompressionAlgorithm.ZLIB
        Compression.BZIP2 -> CompressionAlgorithm.BZIP2
        else -> throw IllegalArgumentException("Unknown compression algorithm \"$name\"")
    }

private fun ai.tech.core.misc.cryptography.model.HashAlgorithm.toHashAlgorithm(): HashAlgorithm =
    when (this) {
        ai.tech.core.misc.cryptography.model.HashAlgorithm.MD5 -> HashAlgorithm.MD5
        ai.tech.core.misc.cryptography.model.HashAlgorithm.SHA1 -> HashAlgorithm.SHA1
        ai.tech.core.misc.cryptography.model.HashAlgorithm.RIPEMD -> HashAlgorithm.RIPEMD160
        ai.tech.core.misc.cryptography.model.HashAlgorithm.SHA224 -> HashAlgorithm.SHA224
        ai.tech.core.misc.cryptography.model.HashAlgorithm.SHA256 -> HashAlgorithm.SHA224
        ai.tech.core.misc.cryptography.model.HashAlgorithm.SHA384 -> HashAlgorithm.SHA384
        ai.tech.core.misc.cryptography.model.HashAlgorithm.SHA512 -> HashAlgorithm.SHA512
        else -> throw IllegalArgumentException("Not supported hash algorithm \"${name}\"")
    }

private fun SymmetricAlgorithm.toSymmetricAlgorithm(): SymmetricKeyAlgorithm =
    when (this) {
        SymmetricAlgorithm.PLAINTEXT -> SymmetricKeyAlgorithm.NULL
        SymmetricAlgorithm.TRIPLEDES -> SymmetricKeyAlgorithm.TRIPLE_DES
        SymmetricAlgorithm.CAST_5 -> SymmetricKeyAlgorithm.CAST5
        SymmetricAlgorithm.BLOWFISH -> SymmetricKeyAlgorithm.BLOWFISH
        SymmetricAlgorithm.AES_128 -> SymmetricKeyAlgorithm.AES_128
        SymmetricAlgorithm.AES_192 -> SymmetricKeyAlgorithm.AES_192
        SymmetricAlgorithm.AES_256 -> SymmetricKeyAlgorithm.AES_256
        SymmetricAlgorithm.TWOFISH -> SymmetricKeyAlgorithm.TWOFISH
    }

private fun PGPKey.toKeySpecBuilders(
    isSubKeyOption: Boolean = false,
    sign: Boolean = false,
): List<KeySpecBuilder> =
    when (this) {
        is RSA ->
            KeyType
                .RSA(RsaLength.entries.single { it.length == size })
                .let { listOf(it, it) }

        is ECC ->
            when (curve) {
                CurveType.CURVE25519, CurveType.ED25519 -> {
                    listOf(
                        KeyType.EDDSA(EdDSACurve._Ed25519),
                        KeyType.XDH(XDHSpec._X25519),
                    )
                }

                CurveType.P256, CurveType.P384, CurveType.P521,
                CurveType.BRAINPOOLP256R1, CurveType.BRAINPOOLP384R1, CurveType.BRAINPOOLP512R1,
                CurveType.SECP256K1,
                    -> {
                    val curve = EllipticCurve.entries.single { it.name == curve.name }
                    listOf(
                        ECDSA.fromCurve(curve),
                        ECDH.fromCurve(curve),
                    )
                }

                else -> throw SOPGPException.UnsupportedProfile("generate-key", curve.name)
            }
    }.let {
        if (isSubKeyOption) {
            if (sign) {
                listOf(KeySpec.getBuilder(it[0], KeyFlag.SIGN_DATA))
            } else {
                listOf(
                    KeySpec.getBuilder(
                        it[1],
                        KeyFlag.ENCRYPT_STORAGE,
                        KeyFlag.ENCRYPT_COMMS,
                    ),
                )
            }
        } else {
            listOf(
                KeySpec.getBuilder(
                    it[0],
                    KeyFlag.CERTIFY_OTHER,
                    KeyFlag.SIGN_DATA,
                ),
                KeySpec.getBuilder(
                    it[1],
                    KeyFlag.ENCRYPT_STORAGE,
                    KeyFlag.ENCRYPT_COMMS,
                ),
            )
        }.onEach { ks ->
            compressionAlgorithms?.let {
                ks.overridePreferredCompressionAlgorithms(
                    *it
                        .map(Compression::toCompressionAlgorithm)
                        .toTypedArray(),
                )
            }

            hashAlgorithms?.let {
                ks.overridePreferredHashAlgorithms(
                    *it
                        .map(ai.tech.core.misc.cryptography.model.HashAlgorithm::toHashAlgorithm)
                        .toTypedArray(),
                )
            }

            symmetricKeyAlgorithms?.let {
                ks.overridePreferredSymmetricKeyAlgorithms(
                    *it
                        .map(SymmetricAlgorithm::toSymmetricAlgorithm)
                        .toTypedArray(),
                )
            }
        }
    }

private fun PGPKeyRing.toArmoredByteArray(): ByteArray = ByteArrayOutputStream().apply {
    val armoredOutputStream = ArmoredOutputStreamFactory.get(this)
    encode(armoredOutputStream)
    armoredOutputStream.close()
}.toByteArray()

public actual suspend fun pgpKeyPair(
    key: PGPKey,
    subKeys: List<PGPSubKeyType>,
    userIDs: List<PGPUserId>,
    expireDate: Long,
    password: String?,
    armored: Boolean,
): ByteArray {
    try {
        return PGPainless
            .buildKeyRing()
            .also { builder ->

                key.toKeySpecBuilders().let {
                    builder.setPrimaryKey(it[0]).addSubkey(it[1])
                }

                subKeys.forEach {
                    builder.addSubkey(
                        (it.key ?: key).toKeySpecBuilders(
                            true,
                            it.sign,
                        )[0],
                    )
                }

                if (password != null) {
                    builder.setPassphrase(Passphrase.fromPassword(password))
                }

                userIDs.forEach { uid ->
                    builder.addUserId(
                        UserId
                            .newBuilder()
                            .also { uidBuilder ->
                                uid.name?.let { uidBuilder.withName(it) }
                                uid.comment?.let { uidBuilder.withComment(it) }
                                uid.email?.let { uidBuilder.withEmail(it) }
                            }.build(),
                    )
                }

                builder.setExpirationDate(
                    if (expireDate > 0) {
                        Date(expireDate * 1000L)
                    } else {
                        null
                    },
                )
            }.build()
            .let { k ->
                if (armored) {
                    k.toArmoredByteArray()
                } else {
                    ByteArrayOutputStream().apply { k.encode(this) }.toByteArray()
                }
            }
    } catch (e: InvalidAlgorithmParameterException) {
        throw SOPGPException.UnsupportedAsymmetricAlgo("Unsupported asymmetric algorithm.", e)
    } catch (e: NoSuchAlgorithmException) {
        throw SOPGPException.UnsupportedAsymmetricAlgo("Unsupported asymmetric algorithm.", e)
    } catch (e: PGPException) {
        throw RuntimeException(e)
    }
}

public actual suspend fun ByteArray.pgpChangeKeyPassword(
    oldPasswords: List<String>,
    password: String?,
    armored: Boolean,
): ByteArray =
    ByteArrayOutputStream().apply {
        sop
            .changeKeyPassword()
            .let {
                if (armored) {
                    it
                } else {
                    it.noArmor()
                }
            }.let {
                oldPasswords.fold(it) { cp, p -> cp.oldKeyPassphrase(p) }
            }.let {
                if (password == null) {
                    it
                } else {
                    it.newKeyPassphrase(password)
                }
            }.keys(ByteArrayInputStream(this@pgpChangeKeyPassword))
            .writeTo(this)
    }.toByteArray()

public actual suspend fun ByteArray.pgpPublicKey(armored: Boolean): ByteArray =
    ByteArrayOutputStream().apply {
        sop
            .extractCert()
            .let {
                if (armored) {
                    it
                } else {
                    it.noArmor()
                }
            }.key(ByteArrayInputStream(this@pgpPublicKey))
            .writeTo(this)
    }.toByteArray()

public actual suspend fun ByteArray.pgpPublicKeys(armored: Boolean): List<ByteArray> =
    ByteArrayInputStream(this).readPublicKeys(false).let {
        if (armored) {
            it.map { it.toArmoredByteArray() }
        } else {
            it.map { ByteArrayOutputStream().apply { it.encode(this) }.toByteArray() }
        }
    }

public actual suspend fun ByteArray.pgpPrivateKeys(armored: Boolean): List<ByteArray> =
    ByteArrayInputStream(this).readSecretKeys(false).let {
        if (armored) {
            it.map { it.toArmoredByteArray() }
        } else {
            it.map { ByteArrayOutputStream().apply { it.encode(this) }.toByteArray() }
        }
    }

private fun KeyRingInfo.toPGPKeyMetadata(): PGPKeyMetadata =
    PGPKeyMetadata(
        fingerprint.toString(),
        userIds.map(PGPUserId::parse),
        creationDate.time,
        primaryKeyExpirationDate?.time ?: 0,
    )

public actual suspend fun ByteArray.pgpKeyMetadata(): PGPKeyMetadata =
    KeyRingInfo(PGPainless.readKeyRing().keyRing(ByteArrayInputStream(this))!!).toPGPKeyMetadata()

public actual suspend fun ByteArray.pgpKeyArmor(): ByteArray =
    ByteArrayOutputStream().apply {
        sop
            .armor()
            .data(ByteArrayInputStream(this@pgpKeyArmor))
            .writeTo(this)
    }.toByteArray()

public actual suspend fun ByteArray.pgpKeyDearmor(): ByteArray =
    ByteArrayOutputStream().apply {
        sop
            .dearmor()
            .data(ByteArrayInputStream(this@pgpKeyDearmor))
            .writeTo(this)
    }.toByteArray()

// one or more secret keys
public actual suspend fun ByteArray.pgpRevokeKey(
    vararg passwords: String, // primary key password(s) if the key(s) are protected
    armored: Boolean,
): ByteArray =
    ByteArrayOutputStream().apply {
        sop
            .revokeKey()
            .let {
                if (armored) {
                    it
                } else {
                    it.noArmor()
                }
            }.let {
                passwords
                    .fold(it) { rk, p ->
                        rk.withKeyPassword(p)
                    }.keys(ByteArrayInputStream(this@pgpRevokeKey))
            }.writeTo(this)
    }.toByteArray()

public actual suspend fun ByteArray.pgpEncrypt(
    encryptionKeys: List<ByteArray>, // It does not matter, if the certificate is ASCII armored or not
    signingKeys: List<ByteArray>?, // Optionally: Sign the message
    signingKeysPasswords: List<String>?, // if signing key is protected
    passwords: List<String>?,
    armored: Boolean,
    isText: Boolean,
): ByteArray =
    ByteArrayOutputStream().apply {
        sop
            .encrypt()
            .let {
                if (armored) {
                    it
                } else {
                    it.noArmor()
                }
            }.let {
                // encrypt for each recipient
                encryptionKeys.fold(it) { e, k -> e.withCert(ByteArrayInputStream(k)) }
            }.let {
                signingKeys?.fold(it) { e, k -> e.signWith(ByteArrayInputStream(k)) } ?: it
            }.let {
                signingKeysPasswords?.fold(it) { e, p -> e.withKeyPassword(p) } ?: it
            }.let {
                passwords?.fold(it) { e, p -> e.withPassword(p) } ?: it
            }.plaintext(ByteArrayInputStream(this@pgpEncrypt))
            .writeTo(this)
    }.toByteArray()

@OptIn(ExperimentalStdlibApi::class)
private fun SignatureVerification.toPGPVerification(vkrc: List<PGPPublicKeyRingCollection>): PGPVerification =
    PGPVerification(
        signature.keyID.toHexString(),
        signingKey?.let { sk ->
            vkrc.any {
                it.any {
                    it.publicKey.keyID == sk.primaryKeyId && it.getPublicKey(sk.subkeyId) != null
                }
            }
        } ?: false,
    )

public actual suspend fun ByteArray.pgpDecrypt(
    decryptionKeys: List<ByteArray>,
    decryptionKeysPasswords: List<String>?, // if decryption key is protected
    verificationKeys: List<ByteArray>?,
    passwords: List<String>?,
): PGPVerifiedResult {
    val consumerOptions = ConsumerOptions.get()
    val protector = MatchMakingSecretKeyRingProtector()

    decryptionKeys.forEach {
        ByteArrayInputStream(it).readSecretKeys(true).forEach {
            protector.addSecretKey(it)
            consumerOptions.addDecryptionKey(it, protector)
        }
    }

    decryptionKeysPasswords?.forEach {
        protector.addPassphrase(Passphrase.fromPassword(it))
    }

    val vkrc =
        (
                verificationKeys?.map { ByteArrayInputStream(it).readPublicKeys(true) }
                    ?: emptyList()
                ).onEach { consumerOptions.addVerificationCerts(it) }

    passwords?.forEach { password ->
        consumerOptions.addDecryptionPassphrase(Passphrase.fromPassword(password))
        password.trimEnd().let {
            if (it != password) {
                consumerOptions.addDecryptionPassphrase(Passphrase.fromPassword(it))
            }
        }
    }

    if (consumerOptions.decryptionKeys.isEmpty() &&
        consumerOptions.decryptionPassphrases.isEmpty() &&
        consumerOptions.sessionKey == null
    ) {
        throw SOPGPException.MissingArg("Missing decryption key, passphrase or session key.")
    }

    val decryptionStream =
        try {
            PGPainless
                .decryptAndOrVerify()
                .onInputStream(ByteArrayInputStream(this))
                .withOptions(consumerOptions)
        } catch (e: MissingDecryptionMethodException) {
            throw SOPGPException.CannotDecrypt(
                "No usable decryption key or password provided.",
                e,
            )
        } catch (e: WrongPassphraseException) {
            throw SOPGPException.KeyIsProtected()
        } catch (e: MalformedOpenPgpMessageException) {
            throw SOPGPException.BadData(e)
        } catch (e: PGPException) {
            throw SOPGPException.BadData(e)
        } catch (e: IOException) {
            throw SOPGPException.BadData(e)
        } finally {
            // Forget passphrases after decryption
            protector.clear()
        }

    return PGPVerifiedResult(
        withContext(Dispatchers.IO) {
            decryptionStream.readAllBytes()
        },
    ) {
        val metadata = decryptionStream.metadata
        if (!metadata.isEncrypted) {
            throw SOPGPException.BadData("Data is not encrypted.")
        }

        metadata.verifiedInlineSignatures.map { it.toPGPVerification(vkrc) }
    }
}

private fun PGPSignMode.toSignMode(): InlineSignAs =
    when (this) {
        PGPSignMode.BINARY -> InlineSignAs.binary
        PGPSignMode.TEXT -> InlineSignAs.text
        PGPSignMode.CLEARTEXT_SIGN -> InlineSignAs.clearsigned
        else -> throw IllegalArgumentException()
    }

public actual suspend fun ByteArray.pgpSign(
    signingKeys: List<ByteArray>,
    signingKeysPasswords: List<String>?,
    mode: PGPSignMode,
    detached: Boolean,
    armored: Boolean,
): ByteArray =
    if (detached) {
        pgpDetachedSign(
            signingKeys,
            signingKeysPasswords,
            armored,
        )
    } else {
        ByteArrayOutputStream().apply {
            sop
                .inlineSign()
                .mode(mode.toSignMode())
                .let {
                    if (armored) {
                        it
                    } else {
                        it.noArmor()
                    }
                }.let {
                    signingKeys.fold(it) { s, k -> s.key(ByteArrayInputStream(k)) } ?: it
                }.let {
                    signingKeysPasswords?.fold(it) { s, p -> s.withKeyPassword(p) } ?: it
                }.data(ByteArrayInputStream(this@pgpSign)).writeTo(this)
        }.toByteArray()
    }

private fun ByteArray.pgpDetachedSign(
    signingKeys: List<ByteArray>,
    signingKeysPasswords: List<String>?,
    armored: Boolean,
): ByteArray =
    ByteArrayOutputStream().apply {
        sop
            .detachedSign()
            .let {
                if (armored) {
                    it
                } else {
                    it.noArmor()
                }
            }.let {
                signingKeys.fold(it) { s, k -> s.key(ByteArrayInputStream(k)) } ?: it
            }.let {
                signingKeysPasswords?.fold(it) { s, p -> s.withKeyPassword(p) } ?: it
            }.data(ByteArrayInputStream(this@pgpDetachedSign)).writeTo(this)
    }.toByteArray()

public actual suspend fun ByteArray.pgpVerify(
    verificationKeys: List<ByteArray>,
    mode: PGPSignMode,
    signatures: List<ByteArray>?
): PGPVerifiedResult =
    try {
        val options = ConsumerOptions.get()

        val vkrc =
            verificationKeys.map { ByteArrayInputStream(it).readPublicKeys(true) }.onEach {
                options.addVerificationCerts(it)
            }
        signatures?.forEach { options.addVerificationOfDetachedSignatures(ByteArrayInputStream(it)) }

        val verificationStream =
            PGPainless.decryptAndOrVerify().onInputStream(ByteArrayInputStream(this)).withOptions(options)

        PGPVerifiedResult(
            withContext(Dispatchers.IO) {
                verificationStream.readAllBytes()
            },
        ) {
            val result = verificationStream.metadata
            val verifications =
                if (result.isUsingCleartextSignatureFramework || !signatures.isNullOrEmpty()) {
                    result.verifiedDetachedSignatures
                } else {
                    result.verifiedInlineSignatures
                }

            if (options.certificateSource.explicitCertificates.isNotEmpty() &&
                verifications.isEmpty()
            ) {
                throw SOPGPException.NoSignature()
            }

            verifications.map { it.toPGPVerification(vkrc) }
        }
    } catch (e: MissingDecryptionMethodException) {
        throw SOPGPException.BadData("Cannot verify encrypted message.", e)
    } catch (e: MalformedOpenPgpMessageException) {
        throw SOPGPException.BadData(e)
    } catch (e: PGPException) {
        throw SOPGPException.BadData(e)
    }
