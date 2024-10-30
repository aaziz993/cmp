package ai.tech.core.misc.cryptography

import ai.tech.core.misc.cryptography.model.ECC
import ai.tech.core.misc.cryptography.model.PGPKeyMetadata
import ai.tech.core.misc.cryptography.model.PGPKey
import ai.tech.core.misc.cryptography.model.PGPSignMode
import ai.tech.core.misc.cryptography.model.PGPSubKeyType
import ai.tech.core.misc.cryptography.model.PGPUserId
import ai.tech.core.misc.cryptography.model.PGPVerifiedResult
import ai.tech.core.misc.type.multiple.startsWith
import dev.whyoleg.cryptography.BinarySize
import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.CryptographyAlgorithmId
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.asymmetric.EC.Curve
import dev.whyoleg.cryptography.algorithms.asymmetric.EC.PublicKey
import dev.whyoleg.cryptography.algorithms.asymmetric.ECDSA
import dev.whyoleg.cryptography.algorithms.asymmetric.ECDSA.SignatureFormat
import dev.whyoleg.cryptography.algorithms.digest.Digest
import dev.whyoleg.cryptography.algorithms.digest.MD5
import dev.whyoleg.cryptography.algorithms.digest.SHA1
import dev.whyoleg.cryptography.algorithms.digest.SHA224
import dev.whyoleg.cryptography.algorithms.digest.SHA256
import dev.whyoleg.cryptography.algorithms.digest.SHA384
import dev.whyoleg.cryptography.algorithms.digest.SHA3_224
import dev.whyoleg.cryptography.algorithms.digest.SHA3_256
import dev.whyoleg.cryptography.algorithms.digest.SHA3_384
import dev.whyoleg.cryptography.algorithms.digest.SHA3_512
import dev.whyoleg.cryptography.algorithms.digest.SHA512
import dev.whyoleg.cryptography.algorithms.symmetric.AES
import dev.whyoleg.cryptography.algorithms.symmetric.HMAC
import dev.whyoleg.cryptography.algorithms.symmetric.SymmetricKeySize
import dev.whyoleg.cryptography.materials.key.KeyDecoder
import dev.whyoleg.cryptography.materials.key.KeyGenerator
import dev.whyoleg.cryptography.operations.hash.Hasher
import dev.whyoleg.cryptography.random.CryptographyRandom
import dev.whyoleg.cryptography.serialization.asn1.BitArray
import dev.whyoleg.cryptography.serialization.asn1.DER
import dev.whyoleg.cryptography.serialization.asn1.ObjectIdentifier
import dev.whyoleg.cryptography.serialization.asn1.modules.PrivateKeyInfo
import dev.whyoleg.cryptography.serialization.asn1.modules.RSA
import dev.whyoleg.cryptography.serialization.asn1.modules.SubjectPublicKeyInfo
import dev.whyoleg.cryptography.serialization.asn1.modules.UnknownKeyAlgorithmIdentifier
import dev.whyoleg.cryptography.serialization.pem.PEM
import dev.whyoleg.cryptography.serialization.pem.PemContent
import dev.whyoleg.cryptography.serialization.pem.PemLabel
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

internal val ARMOR_HEADER: Array<Byte> = "-----BEGIN PGP ".encodeToByteArray().toTypedArray()

internal const val MIN_RSA_KEY_SIZE: Int = 2047

private val provider: CryptographyProvider by lazy { CryptographyProvider.Default }

// //////////////////////////////////////////////////////HASH///////////////////////////////////////////////////////////
private fun hasher(identifier: CryptographyAlgorithmId<Digest>): Hasher =
    provider
        .get(identifier)
        .hasher()

@OptIn(DelicateCryptographyApi::class)
private val md5Hasher: Hasher by lazy { hasher(MD5) }

@OptIn(DelicateCryptographyApi::class)
private val sha1Hasher: Hasher by lazy { hasher(SHA1) }

private val sha224Hasher: Hasher by lazy { hasher(SHA224) }

private val sha256Hasher: Hasher by lazy { hasher(SHA256) }

private val sha384Hasher: Hasher by lazy { hasher(SHA384) }

private val sha512Hasher: Hasher by lazy { hasher(SHA512) }

private val sha3224Hasher: Hasher by lazy { hasher(SHA3_224) }

private val sha3256Hasher: Hasher by lazy { hasher(SHA3_256) }

private val sha3384Hasher: Hasher by lazy { hasher(SHA3_384) }

private val sha3512Hasher: Hasher by lazy { hasher(SHA3_512) }

public suspend fun ByteArray.hashMd5(): ByteArray = md5Hasher.hash(this)

public suspend fun ByteArray.hashSha1(): ByteArray = sha1Hasher.hash(this)

public suspend fun ByteArray.hashSha224(): ByteArray = sha224Hasher.hash(this)

public suspend fun ByteArray.hashSha256(): ByteArray = sha256Hasher.hash(this)

public suspend fun ByteArray.hashSha384(): ByteArray = sha384Hasher.hash(this)

public suspend fun ByteArray.hashSha512(): ByteArray = sha512Hasher.hash(this)

public suspend fun ByteArray.hashSha3224(): ByteArray = sha3224Hasher.hash(this)

public suspend fun ByteArray.hashSha3256(): ByteArray = sha3256Hasher.hash(this)

public suspend fun ByteArray.hashSha3384(): ByteArray = sha3384Hasher.hash(this)

public suspend fun ByteArray.hashSha3512(): ByteArray = sha3512Hasher.hash(this)

// ///////////////////////////////////////////////////////AES///////////////////////////////////////////////////////////

private val aesGcm: AES.GCM by lazy { provider.get(AES.GCM) }

private fun aesGcmKeyGenerator(keySize: SymmetricKeySize = SymmetricKeySize.B256): KeyGenerator<AES.GCM.Key> = aesGcm.keyGenerator(keySize)

private val aesGcmB128KeyGenerator: KeyGenerator<AES.GCM.Key> by lazy { aesGcmKeyGenerator(SymmetricKeySize.B128) }

private val aesGcmB192KeyGenerator: KeyGenerator<AES.GCM.Key> by lazy { aesGcmKeyGenerator(SymmetricKeySize.B192) }

private val aesGcmB256KeyGenerator: KeyGenerator<AES.GCM.Key> by lazy { aesGcmKeyGenerator(SymmetricKeySize.B256) }

private val aesGcmKeyDecoder: KeyDecoder<AES.Key.Format, AES.GCM.Key> by lazy { aesGcm.keyDecoder() }

public suspend fun aesGcmB128Key(): AES.GCM.Key = aesGcmB128KeyGenerator.generateKey()

public suspend fun aesGcmB192Key(): AES.GCM.Key = aesGcmB192KeyGenerator.generateKey()

public suspend fun aesGcmB256Key(): AES.GCM.Key = aesGcmB256KeyGenerator.generateKey()

public suspend fun ByteArray.aesGcmKeyDecode(format: AES.Key.Format): AES.GCM.Key = aesGcmKeyDecoder.decodeFrom(format, this)

public suspend fun ByteArray.encrypt(
    key: AES.GCM.Key,
    tagSize: BinarySize = 128.bits,
): ByteArray = key.cipher(tagSize).encrypt(this)

public suspend fun ByteArray.decrypt(
    key: AES.GCM.Key,
    tagSize: BinarySize = 128.bits,
): ByteArray = key.cipher(tagSize).decrypt(this)

// ///////////////////////////////////////////////////////HMAC//////////////////////////////////////////////////////////

private val hmac: HMAC by lazy { provider.get(HMAC) }

private fun hmacKeyGenerator(digest: CryptographyAlgorithmId<Digest>): KeyGenerator<HMAC.Key> = hmac.keyGenerator(digest)

private fun hmacKeyDecoder(digest: CryptographyAlgorithmId<Digest>): KeyDecoder<HMAC.Key.Format, HMAC.Key> = hmac.keyDecoder(digest)

@OptIn(DelicateCryptographyApi::class)
private val hmacMd5KeyGenerator: KeyGenerator<HMAC.Key> by lazy { hmacKeyGenerator(MD5) }

@OptIn(DelicateCryptographyApi::class)
private val hmacMd5KeyDecoder: KeyDecoder<HMAC.Key.Format, HMAC.Key> by lazy { hmacKeyDecoder(MD5) }

@OptIn(DelicateCryptographyApi::class)
private val hmacSha1KeyGenerator: KeyGenerator<HMAC.Key> by lazy { hmacKeyGenerator(SHA1) }

@OptIn(DelicateCryptographyApi::class)
private val hmacSha1KeyDecoder: KeyDecoder<HMAC.Key.Format, HMAC.Key> by lazy { hmacKeyDecoder(SHA1) }

private val hmacSha224KeyGenerator: KeyGenerator<HMAC.Key> by lazy { hmacKeyGenerator(SHA224) }
private val hmacSha224KeyDecoder: KeyDecoder<HMAC.Key.Format, HMAC.Key> by lazy { hmacKeyDecoder(SHA224) }

private val hmacSha256KeyGenerator: KeyGenerator<HMAC.Key> by lazy { hmacKeyGenerator(SHA256) }
private val hmacSha256KeyDecoder: KeyDecoder<HMAC.Key.Format, HMAC.Key> by lazy { hmacKeyDecoder(SHA256) }

private val hmacSha384KeyGenerator: KeyGenerator<HMAC.Key> by lazy { hmacKeyGenerator(SHA384) }
private val hmacSha384KeyDecoder: KeyDecoder<HMAC.Key.Format, HMAC.Key> by lazy { hmacKeyDecoder(SHA384) }

private val hmacSha512KeyGenerator: KeyGenerator<HMAC.Key> by lazy { hmacKeyGenerator(SHA512) }
private val hmacSha512KeyDecoder: KeyDecoder<HMAC.Key.Format, HMAC.Key> by lazy { hmacKeyDecoder(SHA512) }

private val hmacSha3224KeyGenerator: KeyGenerator<HMAC.Key> by lazy { hmacKeyGenerator(SHA3_224) }
private val hmacSha3224KeyDecoder: KeyDecoder<HMAC.Key.Format, HMAC.Key> by lazy { hmacKeyDecoder(SHA3_224) }

private val hmacSha3256KeyGenerator: KeyGenerator<HMAC.Key> by lazy { hmacKeyGenerator(SHA3_256) }
private val hmacSha3256KeyDecoder: KeyDecoder<HMAC.Key.Format, HMAC.Key> by lazy { hmacKeyDecoder(SHA3_256) }

private val hmacSha3384KeyGenerator: KeyGenerator<HMAC.Key> by lazy { hmacKeyGenerator(SHA3_384) }
private val hmacSha3384KeyDecoder: KeyDecoder<HMAC.Key.Format, HMAC.Key> by lazy { hmacKeyDecoder(SHA3_384) }

private val hmacSha3512KeyGenerator: KeyGenerator<HMAC.Key> by lazy { hmacKeyGenerator(SHA3_512) }
private val hmacSha3512KeyDecoder: KeyDecoder<HMAC.Key.Format, HMAC.Key> by lazy { hmacKeyDecoder(SHA3_512) }

public suspend fun hmacMd5Key(): HMAC.Key = hmacMd5KeyGenerator.generateKey()

public suspend fun ByteArray.hmacMd5KeyDecode(format: HMAC.Key.Format): HMAC.Key = hmacMd5KeyDecoder.decodeFrom(format, this)

public suspend fun hmacSha1Key(): HMAC.Key = hmacSha1KeyGenerator.generateKey()

public suspend fun ByteArray.hmacSha1KeyDecode(format: HMAC.Key.Format): HMAC.Key = hmacSha1KeyDecoder.decodeFrom(format, this)

public suspend fun hmacSha224Key(): HMAC.Key = hmacSha224KeyGenerator.generateKey()

public suspend fun ByteArray.hmacSha224KeyDecode(format: HMAC.Key.Format): HMAC.Key = hmacSha224KeyDecoder.decodeFrom(format, this)

public suspend fun hmacSha256Key(): HMAC.Key = hmacSha256KeyGenerator.generateKey()

public suspend fun ByteArray.hmacSha256KeyDecode(format: HMAC.Key.Format): HMAC.Key = hmacSha256KeyDecoder.decodeFrom(format, this)

public suspend fun hmacSha384Key(): HMAC.Key = hmacSha384KeyGenerator.generateKey()

public suspend fun ByteArray.hmacSha384KeyDecode(format: HMAC.Key.Format): HMAC.Key = hmacSha384KeyDecoder.decodeFrom(format, this)

public suspend fun hmacSha512Key(): HMAC.Key = hmacSha512KeyGenerator.generateKey()

public suspend fun ByteArray.hmacSha512KeyDecode(format: HMAC.Key.Format): HMAC.Key = hmacSha512KeyDecoder.decodeFrom(format, this)

public suspend fun hmacSha3224Key(): HMAC.Key = hmacSha3224KeyGenerator.generateKey()

public suspend fun ByteArray.hmacSha3224KeyDecode(format: HMAC.Key.Format): HMAC.Key = hmacSha3224KeyDecoder.decodeFrom(format, this)

public suspend fun hmacSha3256Key(): HMAC.Key = hmacSha3256KeyGenerator.generateKey()

public suspend fun ByteArray.hmacSha3256KeyDecode(format: HMAC.Key.Format): HMAC.Key = hmacSha3256KeyDecoder.decodeFrom(format, this)

public suspend fun hmacSha3384Key(): HMAC.Key = hmacSha3384KeyGenerator.generateKey()

public suspend fun ByteArray.hmacSha3384KeyDecode(format: HMAC.Key.Format): HMAC.Key = hmacSha3384KeyDecoder.decodeFrom(format, this)

public suspend fun hmacSha3512Key(): HMAC.Key = hmacSha3512KeyGenerator.generateKey()

public suspend fun ByteArray.hmacSha3512KeyDecode(format: HMAC.Key.Format): HMAC.Key = hmacSha3512KeyDecoder.decodeFrom(format, this)

public suspend fun ByteArray.hmacSignature(key: HMAC.Key): ByteArray = key.signatureGenerator().generateSignature(this)

public suspend fun ByteArray.hmacVerifySignature(
    key: HMAC.Key,
    signature: ByteArray,
): Boolean = key.signatureVerifier().verifySignature(this, signature)

// //////////////////////////////////////////////////////ECDSA//////////////////////////////////////////////////////////
private val ecdsa: ECDSA by lazy { provider.get(ECDSA) }

private fun ecdsaKeyGenerator(curve: Curve): KeyGenerator<ECDSA.KeyPair> = ecdsa.keyPairGenerator(curve)

private fun ecdsaPublicKeyDecoder(curve: Curve): KeyDecoder<PublicKey.Format, ECDSA.PublicKey> = ecdsa.publicKeyDecoder(curve)

private val ecdsaP256KeyGenerator: KeyGenerator<ECDSA.KeyPair> by lazy { ecdsaKeyGenerator(Curve.P256) }
private val ecdsaP256PublicKeyDecoder: KeyDecoder<PublicKey.Format, ECDSA.PublicKey> by lazy {
    ecdsaPublicKeyDecoder(Curve.P256)
}

private val ecdsaP384KeyGenerator: KeyGenerator<ECDSA.KeyPair> by lazy { ecdsaKeyGenerator(Curve.P384) }
private val ecdsaP384PublicKeyDecoder: KeyDecoder<PublicKey.Format, ECDSA.PublicKey> by lazy {
    ecdsaPublicKeyDecoder(Curve.P384)
}

private val ecdsaP521KeyGenerator: KeyGenerator<ECDSA.KeyPair> by lazy { ecdsaKeyGenerator(Curve.P521) }
private val ecdsaP521PublicKeyDecoder: KeyDecoder<PublicKey.Format, ECDSA.PublicKey> by lazy {
    ecdsaPublicKeyDecoder(Curve.P521)
}

public suspend fun ecdsaP256KeyPair(): ECDSA.KeyPair = ecdsaP256KeyGenerator.generateKey()

public suspend fun ecdsaP384KeyPair(): ECDSA.KeyPair = ecdsaP384KeyGenerator.generateKey()

public suspend fun ecdsaP521KeyPair(): ECDSA.KeyPair = ecdsaP521KeyGenerator.generateKey()

public suspend fun ByteArray.ecdsaP256PublicKeyDecode(format: PublicKey.Format): ECDSA.PublicKey =
    ecdsaP256PublicKeyDecoder.decodeFrom(format, this)

public suspend fun ByteArray.ecdsaP384PublicKeyDecode(format: PublicKey.Format): ECDSA.PublicKey =
    ecdsaP384PublicKeyDecoder.decodeFrom(format, this)

public suspend fun ByteArray.ecdsaP521PublicKeyDecode(format: PublicKey.Format): ECDSA.PublicKey =
    ecdsaP521PublicKeyDecoder.decodeFrom(format, this)

public suspend fun ByteArray.ecdsaP521Signature(
    key: ECDSA.PrivateKey,
    digest: CryptographyAlgorithmId<Digest>,
    format: SignatureFormat = SignatureFormat.RAW,
): ByteArray = key.signatureGenerator(digest, format).generateSignature(this)

public suspend fun ByteArray.ecdsaP521VerifySignature(
    key: ECDSA.PublicKey,
    signature: ByteArray,
    digest: CryptographyAlgorithmId<Digest>,
    format: SignatureFormat = SignatureFormat.RAW,
): Boolean = key.signatureVerifier(digest, format).verifySignature(this, signature)

// ///////////////////////////////////////////////SECURE RANDOM/////////////////////////////////////////////////////////

public fun secureRandom(size: Int): ByteArray = CryptographyRandom.nextBytes(size)

// ////////////////////////////////////////////////////PEM//////////////////////////////////////////////////////////////
public fun ByteArray.encodePEM(label: String): String =
    PEM.encode(
        PemContent(
            label = PemLabel(label),
            bytes = this,
        ),
    )

public fun String.decodePEM(): PemContent = PEM.decode(this)

// ////////////////////////////////////////////////////DER//////////////////////////////////////////////////////////////
public inline fun <reified T> T.encodeDER(): ByteArray = DER.encodeToByteArray(this)

public inline fun <reified T> ByteArray.decodeDER(): T = DER.decodeFromByteArray(this)

public fun ByteArray.encodeDERPublicRSAKey(): ByteArray =
    SubjectPublicKeyInfo(UnknownKeyAlgorithmIdentifier(ObjectIdentifier.RSA), BitArray(0, this)).encodeDER()

public fun ByteArray.decodeDERPublicRSAKey(): SubjectPublicKeyInfo = decodeDER()

public fun ByteArray.encodeDERPrivateRSAKey(version: Int): ByteArray =
    PrivateKeyInfo(version, UnknownKeyAlgorithmIdentifier(ObjectIdentifier.RSA), this).encodeDER()

public fun ByteArray.decodeDERPrivateRSAKey(version: Int): PrivateKeyInfo = decodeDER()

// ///////////////////////////////////////////////////////PGP///////////////////////////////////////////////////////////

public expect suspend fun pgpKeyPair(
    key: PGPKey = ECC(),
    subKeys: List<PGPSubKeyType> = emptyList(),
    userIDs: List<PGPUserId>,
    // Number of seconds from the key creation time after which the key expires.
    // null or 0 (never expires)
    expireDate: Long = 0,
    // The password used to encrypt the generated private key. If omitted or empty, the key won't be encrypted.
    password: String? = null,
    // Format of the output keys
    armored: Boolean = true,
): ByteArray

public expect suspend fun ByteArray.pgpChangeKeyPassword(
    oldPasswords: List<String>,
    password: String?,
    armored: Boolean,
): ByteArray

public expect suspend fun ByteArray.pgpPublicKey(armored: Boolean = true): ByteArray

public expect suspend fun ByteArray.pgpPrivateKeys(armored: Boolean): List<ByteArray>

public expect suspend fun ByteArray.pgpPublicKeys(armored: Boolean): List<ByteArray>

public expect suspend fun ByteArray.pgpKeyMetadata(): PGPKeyMetadata

public expect suspend fun ByteArray.pgpKeyArmor(): ByteArray

public expect suspend fun ByteArray.pgpKeyDearmor(): ByteArray

public expect suspend fun ByteArray.pgpRevokeKey(
    vararg passwords: String,
    armored: Boolean = true,
): ByteArray

public expect suspend fun ByteArray.pgpEncrypt(
    encryptionKeys: List<ByteArray>,
    signingKeys: List<ByteArray>? = null,
    signingKeysPasswords: List<String>? = null,
    passwords: List<String>? = null,
    armored: Boolean = true,
    isText: Boolean = true,
): ByteArray

public expect suspend fun ByteArray.pgpDecrypt(
    decryptionKeys: List<ByteArray>,
    decryptionKeysPasswords: List<String>? = null,
    verificationKeys: List<ByteArray>? = null,
    passwords: List<String>? = null,
): PGPVerifiedResult

public expect suspend fun ByteArray.pgpSign(
    signingKeys: List<ByteArray>,
    signingKeysPasswords: List<String>? = null,
    mode: PGPSignMode = PGPSignMode.CLEARTEXT_SIGN,
    detached: Boolean = false,
    armored: Boolean = true,
): ByteArray

public expect suspend fun ByteArray.pgpVerify(
    verificationKeys: List<ByteArray>,
    mode: PGPSignMode = PGPSignMode.CLEARTEXT_SIGN,
    signatures: List<ByteArray>? = null,
): PGPVerifiedResult

public val ByteArray.isPGPArmored: Boolean
    get() = iterator().startsWith(*ARMOR_HEADER)
