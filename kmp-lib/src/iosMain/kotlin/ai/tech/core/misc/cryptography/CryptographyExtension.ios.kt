package ai.tech.core.misc.cryptography

import ai.tech.core.data.model.Compression
import ai.tech.core.misc.cryptography.model.Config
import ai.tech.core.misc.cryptography.model.CreateCleartextMessageOptions
import ai.tech.core.misc.cryptography.model.CreateMessageOptions
import ai.tech.core.misc.cryptography.model.Curve
import ai.tech.core.misc.cryptography.model.ECC
import ai.tech.core.misc.cryptography.model.GenerateKeyOptions
import ai.tech.core.misc.cryptography.model.HashAlgorithm
import ai.tech.core.misc.cryptography.model.PGPKeyMetadata
import ai.tech.core.misc.cryptography.model.PGPKey
import ai.tech.core.misc.cryptography.model.PGPSignMode
import ai.tech.core.misc.cryptography.model.PGPSubKeyType
import ai.tech.core.misc.cryptography.model.PGPUserId
import ai.tech.core.misc.cryptography.model.PGPVerification
import ai.tech.core.misc.cryptography.model.PGPVerifiedResult
import ai.tech.core.misc.cryptography.model.RSA
import ai.tech.core.misc.cryptography.model.ReadKeyOptions
import ai.tech.core.misc.cryptography.model.ReadKeysOptions
import ai.tech.core.misc.cryptography.model.ReadMessageOptions
import ai.tech.core.misc.cryptography.model.ReadSignatureOptions
import ai.tech.core.misc.cryptography.model.SubkeyOptions
import ai.tech.core.misc.cryptography.model.SymmetricAlgorithm
import ai.tech.core.misc.type.multiple.decode
import ai.tech.core.misc.type.multiple.encode
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

    return openpgpJsContext.evaluateScript(
        """(async () => {
            return await openpgp.generateKey(${
            openpgpJson.encodeToString(
                GenerateKeyOptions(
                    userIDs,
                    password,
                    keyType,
                    curve,
                    rsaBits,
                    expireDate.toDouble(),
                    subKeys.map {
                        when (it.key) {
                            is ECC -> SubkeyOptions("ecc", curveMap[it.key.curve], sign = it.sign)
                            is RSA -> SubkeyOptions("rsa", rsaBits = it.key.size.toDouble(), sign = it.sign)
                            else -> SubkeyOptions(keyType, curve, rsaBits, it.sign)
                        }
                    },
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
        }).then((k) => $armored ? k.privateKey : new TextDecoder().decode(k.privateKey));
        })();""",
    )!!.toString().encode()
}

private fun ByteArray.readKey(): String = """openpgp.readKey(${
    openpgpJson.encodeToString(
        if (isPGPArmored) {
            ReadKeyOptions(decode())
        }
        else {
            ReadKeyOptions(binaryKey = this)
        },
    )
})
"""

private fun ByteArray.readPrivateKey(): String = """openpgp.readPrivateKey(${
    openpgpJson.encodeToString(
        if (isPGPArmored) {
            ReadKeyOptions(decode())
        }
        else {
            ReadKeyOptions(binaryKey = this)
        },
    )
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

    return openpgpJsContext.evaluateScript(
        """(async () => {
            return await ${readPrivateKey()}.then((k)=>openpgp.decryptKey({
                privateKey: k,
                passphrase: ${openpgpJson.encodeToString(oldPasswords)}
            })).then((dk)=>${
            if (password == null) {
                "Promise.resolve(dk)"
            }
            else {
                """openpgp.encryptKey({
                        privateKey: dk,
                        passphrase: [$password]
                    })"""
            }
        }).then((k)=>armored ? k : new TextDecoder().decode(k));
    })();""",
    )!!.toString().encode()
}

public actual suspend fun ByteArray.pgpPublicKey(armored: Boolean): ByteArray = openpgpJsContext.evaluateScript(
    """(async () => {
            return await ${readKey()}.then((k)=>k.toPublic()).then((k)=>$armored ? k.armor() : new TextDecoder().decode(k.write()));
        })();""",
)!!.toString().encode()

public actual suspend fun ByteArray.pgpPrivateKeys(armored: Boolean): List<ByteArray> = openpgpJsContext.evaluateScript(
    """(async () => {
            return await openpgp.readPrivateKeys(${
        openpgpJson.encodeToString(
            if (isPGPArmored) {
                ReadKeysOptions(decode())
            }
            else {
                ReadKeyOptions(binaryKey = this)
            },
        )
    }).then((ks)=>ks.map((k)=>$armored ? k.armor() : new TextDecoder().decode(k.write())));
        })();""",
)!!.toArray()!!.map { it.toString().encode() }

public actual suspend fun ByteArray.pgpPublicKeys(armored: Boolean): List<ByteArray> = openpgpJsContext.evaluateScript(
    """(async () => {
            return await openpgp.readKeys(${
        openpgpJson.encodeToString(
            if (isPGPArmored) {
                ReadKeysOptions(decode())
            }
            else {
                ReadKeyOptions(binaryKey = this)
            },
        )
    }).then((ks)=>ks.map((k)=>$armored ? k.armor() : new TextDecoder().decode(k.write())));
        })();""",
)!!.toArray()!!.map { it.toString().encode() }

public actual suspend fun ByteArray.pgpKeyMetadata(): PGPKeyMetadata = openpgpJsContext.evaluateScript(
    """(async function() {
           await ${readKey()}.then(async (k) => ({
                fingerprint: k.getFingerprint(),
                userIDs: k.getUserIDs(),
                createDate: k.getCreationTime().getTime(),
                expireDate: await k.getExpirationTime().then((t) =>{
                    if(t==null){
                        return 0
                    }
                    return t === Infinity ? -1 : t
                })
            }))
        })();""",
)!!.let(JSValue::toDictionary)!!.let {
    PGPKeyMetadata(
        it["fingerprint"].toString(),
        userIDs = (it["userIDs"] as List<String>).map(PGPUserId::parse),
        createDate = it["createDate"] as Long,
        expireDate = it["expireDate"] as Long,
    )
}

public actual suspend fun ByteArray.pgpKeyArmor(): ByteArray = openpgpJsContext.evaluateScript(
    """(async function() {
            return await ${readKey()}.then((k)=>k.armor());
        })();""",
)!!.toString().encode()

public actual suspend fun ByteArray.pgpKeyDearmor(): ByteArray = openpgpJsContext.evaluateScript(
    """(async function() {
            return await ${readKey()}.then((k)=>new TextDecoder().decode(k.write()));
        })();""",
)!!.toString().encode()

public actual suspend fun ByteArray.pgpRevokeKey(
    vararg passwords: String,
    armored: Boolean,
): ByteArray = openpgpJsContext.evaluateScript(
    """(async function() {
            return await openpgp.revokeKey({
                key: await ${readPrivateKey()},
                format: $armored ? 'armored' : 'binary'
            }).then((k) => $armored ? k : new TextDecoder().decode(k));
        })();
    """,
)!!.toString().encode()

private fun List<ByteArray>.readDecrytedKeys(passwords: List<String>?): String =
    "Promise.all(${
        map {
            """${it.readPrivateKey()}.then((k)=> k.isDecrypted() ? Promise.resolve(k) :
                    openpgp.decryptKey({
                        privateKey: k,
                        passphrase: ${openpgpJson.encodeToString(passwords.orEmpty())}
                    }))"""
        }
    })"

public actual suspend fun ByteArray.pgpEncrypt(
    encryptionKeys: List<ByteArray>,
    signingKeys: List<ByteArray>?,
    signingKeysPasswords: List<String>?,
    passwords: List<String>?,
    armored: Boolean,
    isText: Boolean,
): ByteArray = openpgpJsContext.evaluateScript(
    """(async function() {
        return await openpgp.encrypt({
                    message: await openpgp.createMessage(
                ${
        openpgpJson.encodeToString(
            if (isText) {
                CreateMessageOptions(decode())
            }
            else {
                CreateMessageOptions(binary = this)
            },
        )
    }),
                encryptionKeys: await Promise.all(${encryptionKeys.map { it.readKey() }}),
                ${signingKeys?.let { "signingKeys: await ${signingKeys.readDecrytedKeys(signingKeysPasswords)}," }}
                ${passwords?.let { "passwords: ${openpgpJson.encodeToString(it)}," } ?: ""}
                format: $armored? 'armored' : 'binary'
            }).then(m=> $armored ? m : new TextDecoder().decode(m))
        })();""",
)!!.toString().encode()

public actual suspend fun ByteArray.pgpDecrypt(
    decryptionKeys: List<ByteArray>,
    decryptionKeysPasswords: List<String>?,
    verificationKeys: List<ByteArray>?,
    passwords: List<String>?,
): PGPVerifiedResult = openpgpJsContext.evaluateScript(
    """(async function() {
            return await openpgp.readMessage(${
        if (isPGPArmored) {
            ReadMessageOptions(decode())
        }
        else {
            ReadMessageOptions(binaryMessage = this)
        }
    }).then(async (m)=>openpgp.decrypt({
         decryptionKeys: await ${decryptionKeys.readDecrytedKeys(decryptionKeysPasswords)},
         ${verificationKeys?.let { "verificationKeys: await Promise.all(${verificationKeys.map { it.readKey() }})," }}
         ${passwords?.let { "passwords: ${openpgpJson.encodeToString(it)}" }}
    })).then(async (dr)=>({
        data: dr.data,
        verifications: dr.signatures.map((s)=>({
            keyId: s.keyID.toHex(),
            verified: s.verified.catch { false }
        }))
    }))
        })();""",
)!!.toDictionary()!!.let {
    PGPVerifiedResult(
        it["data"]!!.toString().encode(),
        {
            (it["verifications"] as List<Map<String, *>>).map {
                PGPVerification(
                    it["keyID"]!!.toString(),
                    it["verified"]!! as Boolean,
                )
            }
        },
    )
}

public actual suspend fun ByteArray.pgpSign(
    signingKeys: List<ByteArray>,
    signingKeysPasswords: List<String>?,
    mode: PGPSignMode,
    detached: Boolean,
    armored: Boolean,
): ByteArray = openpgpJsContext.evaluateScript(
    """(async function() {
            return await openpgp.sign({
                message: await ${
        when (mode) {
            PGPSignMode.BINARY -> "openpgp.createMessage(${openpgpJson.encodeToString(CreateMessageOptions(binary = this))})"
            PGPSignMode.TEXT -> "openpgp.createMessage(${openpgpJson.encodeToString(CreateMessageOptions(decode()))})"
            PGPSignMode.CLEARTEXT_SIGN -> "openpgp.createCleartextMessage(${openpgpJson.encodeToString(CreateCleartextMessageOptions(decode()))})"
        }
    },
                signingKeys: await ${signingKeys.readDecrytedKeys(signingKeysPasswords)},
                detached: $detached,
                format: $armored ? 'armored' : 'binary'
            }).then((m)=>$armored ? m : new TextDecoder().decode(m))
        })();""",
)!!.toString().encode()

public actual suspend fun ByteArray.pgpVerify(
    verificationKeys: List<ByteArray>,
    mode: PGPSignMode,
    signatures: List<ByteArray>?
): PGPVerifiedResult {
    require(signatures == null || signatures.size == 1) {
        "Only one or zero signatures can be used to verify"
    }

    return openpgpJsContext.evaluateScript(
        """(async function() {
                return await openpgp.verify({
                    message: await ${
            when (mode) {
                PGPSignMode.BINARY -> "openpgp.createMessage(${openpgpJson.encodeToString(CreateMessageOptions(binary = this))})"
                PGPSignMode.TEXT -> "openpgp.createMessage(${openpgpJson.encodeToString(CreateMessageOptions(decode()))})"
                PGPSignMode.CLEARTEXT_SIGN -> "openpgp.createCleartextMessage(${openpgpJson.encodeToString(CreateCleartextMessageOptions(decode()))})"
            }
        },
                   ${
            signatures?.first()?.let {
                "signature: await openpgp.readSignature(${
                    openpgpJson.encodeToString(
                        if (it.isPGPArmored) {
                            ReadSignatureOptions(it.decode())
                        }
                        else {
                            ReadSignatureOptions(binarySignature = it)
                        },
                    )
                }),"
            }
        }
        verificationKeys: await Promise.all(${verificationKeys.map { it.readKey() }})
                }).then(vr=>({
                    data: ${if (mode == PGPSignMode.CLEARTEXT_SIGN){"vr.data"}else{"new TextDecoder().decode(vr.data)"}},
                    verifications: dr.signatures.map((s)=>({
                        keyId: s.keyID.toHex(),
                        verified: s.verified.catch { false }
                    }))
                }))

        })();""",
    )!!.toDictionary()!!.let {
        PGPVerifiedResult(
            it["data"]!!.toString().encode(),
            {
                (it["verifications"] as List<Map<String, *>>).map {
                    PGPVerification(
                        it["keyID"]!!.toString(),
                        it["verified"]!! as Boolean,
                    )
                }
            },
        )
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
