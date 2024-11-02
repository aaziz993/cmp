package ai.tech.core.misc.cryptography

import ai.tech.core.data.model.Compression
import ai.tech.core.misc.cryptography.Key
import ai.tech.core.misc.cryptography.PrivateKey
import ai.tech.core.misc.cryptography.model.Config
import ai.tech.core.misc.cryptography.model.Curve
import ai.tech.core.misc.cryptography.model.ECC
import ai.tech.core.misc.cryptography.model.GenerateKeyOptions
import ai.tech.core.misc.cryptography.model.HashAlgorithm
import ai.tech.core.misc.cryptography.model.PGPKeyMetadata
import ai.tech.core.misc.cryptography.model.PGPKey
import ai.tech.core.misc.cryptography.model.PGPSignMode
import ai.tech.core.misc.cryptography.model.PGPSubKeyType
import ai.tech.core.misc.cryptography.model.PGPUserId
import ai.tech.core.misc.cryptography.model.PGPVerifiedResult
import ai.tech.core.misc.cryptography.model.RSA
import ai.tech.core.misc.cryptography.model.ReadKeyOptions
import ai.tech.core.misc.cryptography.model.SymmetricAlgorithm
import ai.tech.core.misc.type.Object
import ai.tech.core.misc.type.multiple.decode
import ai.tech.core.misc.type.multiple.encode
import js.promise.Promise
import js.typedarrays.toUint8Array
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import platform.JavaScriptCore.*
import platform.Foundation.*

private val openpgpJson = Json {
    explicitNulls = false
}

private val openpgpJsContext = JSContext().apply {
    // Load openpgp.min.js
    val openPgpJsPath = NSBundle.mainBundle.pathForResource("openpgp.min", "js")!!
    val openPgpScript = NSString.stringWithContentsOfFile(openPgpJsPath, NSUTF8StringEncoding, null) as String
    evaluateScript(openPgpScript)
}

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
    var curve: Curve? = null
    var rsaBits: Double? = null

    when (key) {
        is ECC -> {
            keyType = "ecc"
            curve = key.curve
        }

        is RSA -> {
            require(key.size >= MIN_RSA_KEY_SIZE) {
                "RSA size should be at least $MIN_RSA_KEY_SIZE, got: ${key.size}"
            }

            keyType = "rsa"
            rsaBits = key.size.toDouble()
        }
    }

    return openpgpJsContext.evaluateScript(
        """
        (async () => {
            return await openpgp.generateKey(${
            openpgpJson.encodeToString(
                GenerateKeyOptions(
                    userIDs,
                    password,
                    keyType,
                    "",
                    rsaBits,
                    expireDate.toDouble(),
                    emptyList(),
                    if (armored) "'armored'" else "'binary'",
                    Config(
                        key.compressionAlgorithms?.let {
                            require(it.size == 1) {
                                "Only one compression algorithm allowed"
                            }
                            compressionMap[it.single()]!!
                        },
                        key.hashAlgorithms?.let {
                            require(it.size == 1) {
                                "Only one symmetric algorithm allowed"
                            }
                            hashAlgorithmMap[it.single()]!!
                        },
                        key.symmetricKeyAlgorithms?.let {
                            require(it.size == 1) {
                                "Only one symmetric algorithm allowed"
                            }
                            symmetricAlgorithmMap[it.single()]!!
                        },
                    ),
                ),
            )
        }).then(k => $armored ? k.privateKey : new TextDecoder().decode(k.privateKey));
        })();
    """.trimIndent(),
    )!!.toString().encode()
}

private fun ByteArray.readKey(): String = """openpgp.readKey(${
    openpgpJson.encodeToString(if (isPGPArmored) {
        ReadKeyOptions(decode())
    }
    else {
        ReadKeyOptions(binaryKey = this)
    })
})
"""

private fun ByteArray.readPrivateKey(): String = """openpgp.readPrivateKey(${
   openpgpJson.encodeToString(if (isPGPArmored) {
        ReadKeyOptions(decode())
    }
    else {
        ReadKeyOptions(binaryKey = this)
    })
})
"""

public actual suspend fun ByteArray.pgpChangeKeyPassword(
    oldPasswords: List<String>,
    password: String?,
    armored: Boolean,
): ByteArray {
    require(oldPasswords.size == 1) {
        "Only one of old password allowed"
    }

    val oldPassphrases = oldPasswords.joinToString(",") { "'$it'" }
    val jsScript = """
        (async function() {
            const privateKey = await ${readPrivateKey()}.then(k=>);


            const newKey = await openpgp.changeKeyPassword({
                privateKey: privateKey,
                passphrase: ${password?.let { "'$it'" } ?: "null"},
                format: ${if (armored) "'armored'" else "'binary'"}
            });
            return ${if (armored) "newKey.armor()" else "newKey"};
        })();
    """.trimIndent()
    val result = openpgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpPublicKey(armored: Boolean): ByteArray {
    val jsScript = """
        (async function() {
            const key = await openpgp.readKey({ armoredKey: ${decodeToString()} });
            return key.toPublic().${if (armored) "armor()" else "toByteArray()"};
        })();
    """.trimIndent()
    val result = openpgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpPrivateKeys(armored: Boolean): List<ByteArray> {
    val jsScript = """
        (async function() {
            const key = await openpgp.readKey({ armoredKey: ${decodeToString()} });
            const privateKeys = key.getKeys().map(k => k.toPrivate());
            return privateKeys.map(k => k.${if (armored) "armor()" else "toByteArray()"});
        })();
    """.trimIndent()
    val result = openpgpJsContext.evaluateScript(jsScript)
    return (result as JSValue).toArray()!!.map { it.toString().encodeToByteArray() }
}

public actual suspend fun ByteArray.pgpPublicKeys(armored: Boolean): List<ByteArray> {
}

public actual suspend fun ByteArray.pgpKeyMetadata(): PGPKeyMetadata {
    val jsScript = """
        (async function() {
            const key = await openpgp.readKey({ armoredKey: "${decodeToString()}" });
            return {
                keyID: key.getKeyID().toHex(),
                creationTime: key.getCreationTime().getTime(),
                expirationTime: key.getExpirationTime() ? key.getExpirationTime().getTime() : null,
                algorithm: key.getAlgorithm(),
                bitSize: key.getBitSize(),
                fingerprint: key.getFingerprint().toString(),
                isPublicKey: key.isPublic(),
                userIDs: key.getUserIDs()
            };
        })();
    """.trimIndent()

    // Evaluate the script in the JSContext
    val result = openpgpJsContext.evaluateScript(jsScript)

    // Extract values from the JS object
    val metadataJsValue = result!!.toObject() as NSDictionary
    return PGPKeyMetadata(
        keyID = metadataJsValue.objectForKey("keyID")?.toString() ?: "",
        creationTime = metadataJsValue.objectForKey("creationTime")?.toString()?.toLongOrNull() ?: 0L,
        expirationTime = metadataJsValue.objectForKey("expirationTime")?.toString()?.toLongOrNull(),
        algorithm = metadataJsValue.objectForKey("algorithm")?.toString() ?: "",
        bitSize = metadataJsValue.objectForKey("bitSize")?.toString()?.toIntOrNull() ?: 0,
        fingerprint = metadataJsValue.objectForKey("fingerprint")?.toString() ?: "",
        isPublicKey = metadataJsValue.objectForKey("isPublicKey")?.toBoolean() ?: false,
        userIDs = metadataJsValue.objectForKey("userIDs")?.toArray()?.map { it.toString() } ?: emptyList(),
    )
}

public actual suspend fun ByteArray.pgpKeyArmor(): ByteArray {
    val jsScript = """
        (async function() {
            const key = await openpgp.readKey({ armoredKey: "${decodeToString()}" });
            return key.armor();
        })();
    """.trimIndent()
    val result = openpgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpKeyDearmor(): ByteArray {
    val jsScript = """
        (async function() {
            const key = await openpgp.readKey({ armoredKey: "${decodeToString()}" });
            return key.toByteArray();
        })();
    """.trimIndent()
    val result = openpgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpRevokeKey(
    vararg passwords: String,
    armored: Boolean,
): ByteArray {
    val passwordsJsArray = JSValue.makeArray(openpgpJsContext, passwords.map { "'$it'" }.toTypedArray(), null)
    val jsScript = """
        (async function() {
            const privateKey = await openpgp.readPrivateKey({ armoredKey: "${decodeToString()}" });
            const revoked = await openpgp.revokeKey({
                privateKey,
                passphrase: $passwordsJsArray
            });
            return revoked.${if (armored) "armor()" else "toByteArray()"};
        })();
    """.trimIndent()
    val result = openpgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpEncrypt(
    encryptionKeys: List<ByteArray>,
    signingKeys: List<ByteArray>?,
    signingKeysPasswords: List<String>?,
    passwords: List<String>?,
    armored: Boolean,
    isText: Boolean,
): ByteArray {
    JSValue.makeArray(
        openpgpJsContext
        val encryptionKeysJsArray = , encryptionKeys.map { it.decodeToString() }.toTypedArray(), null)
    val signingKeysJsArray = JSValue.makeArray(
        openpgpJsContext,
        signingKeys?.map { it.decodeToString() }?.toTypedArray()
            ?: arrayOf(),
        null,
    )
    val passwordsJsArray = JSValue.makeArray(openpgpJsContext, passwords?.toTypedArray() ?: arrayOf(), null)
    val jsScript = """
        (async function() {
            const message = await openpgp.createMessage({ text: "${decodeToString()}" });
            const encrypted = await openpgp.encrypt({
                message,
                encryptionKeys: $encryptionKeysJsArray,
                signingKeys: $signingKeysJsArray,
                passwords: $passwordsJsArray,
                format: ${if (armored) "'armored'" else "'binary'"},
                text: ${isText}
            });
            return encrypted;
        })();
    """.trimIndent()
    val result = openpgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpDecrypt(
    decryptionKeys: List<ByteArray>,
    decryptionKeysPasswords: List<String>?,
    verificationKeys: List<ByteArray>?,
    passwords: List<String>?,
): PGPVerifiedResult {
    val decryptionKeysJsArray = JSValue.makeArray(openpgpJsContext, decryptionKeys.map { it.decodeToString() }.toTypedArray(), null)
    val verificationKeysJsArray = JSValue.makeArray(
        openpgpJsContext,
        verificationKeys?.map { it.decodeToString() }?.toTypedArray()
            ?: arrayOf(),
        null,
    )
    val passwordsJsArray = JSValue.makeArray(openpgpJsContext, passwords?.toTypedArray() ?: arrayOf(), null)
    val jsScript = """
        (async function() {
            const message = await openpgp.readMessage({ armoredMessage: "${decodeToString()}" });
            const decrypted = await openpgp.decrypt({
                message,
                decryptionKeys: $decryptionKeysJsArray,
                verificationKeys: $verificationKeysJsArray,
                passwords: $passwordsJsArray
            });
            return decrypted.data;
        })();
    """.trimIndent()
    val result = openpgpJsContext.evaluateScript(jsScript)
    return PGPVerifiedResult(result.toString().encodeToByteArray())
}

public actual suspend fun ByteArray.pgpSign(
    signingKeys: List<ByteArray>,
    signingKeysPasswords: List<String>?,
    mode: PGPSignMode,
    detached: Boolean,
    armored: Boolean,
): ByteArray {
    val signingKeysJsArray = JSValue.makeArray(openpgpJsContext, signingKeys.map { it.decodeToString() }.toTypedArray(), null)
    val passwordsJsArray = JSValue.makeArray(openpgpJsContext, signingKeysPasswords?.toTypedArray() ?: arrayOf(), null)
    val jsScript = """
        (async function() {
            const message = await openpgp.createMessage({ text: "${decodeToString()}" });
            const signed = await openpgp.sign({
                message,
                signingKeys: $signingKeysJsArray,
                passwords: $passwordsJsArray,
                detached: $detached,
                format: ${if (armored) "'armored'" else "'binary'"}
            });
            return signed;
        })();
    """.trimIndent()
    val result = openpgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpVerify(
    verificationKeys: List<ByteArray>,
    mode: PGPSignMode,
    signatures: List<ByteArray>?
): PGPVerifiedResult {
    val verificationKeysJsArray = JSValue.makeArray(openpgpJsContext, verificationKeys.map { it.decodeToString() }.toTypedArray(), null)
    val signaturesJsArray = JSValue.makeArray(
        openpgpJsContext,
        signatures?.map { it.decodeToString() }?.toTypedArray()
            ?: arrayOf(),
        null,
    )
    val jsScript = """
        (async function() {
            const message = await openpgp.createMessage({ text: "${decodeToString()}" });
            const verified = await openpgp.verify({
                message,
                verificationKeys: $verificationKeysJsArray,
                signature: $signaturesJsArray
            });
            return verified.data;
        })();
    """.trimIndent()
    val result = openpgpJsContext.evaluateScript(jsScript)
    return PGPVerifiedResult(result.toString().encodeToByteArray())
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
