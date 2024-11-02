package ai.tech.core.misc.cryptography

import ai.tech.core.misc.cryptography.model.PGPKeyMetadata
import ai.tech.core.misc.cryptography.model.PGPKey
import ai.tech.core.misc.cryptography.model.PGPSignMode
import ai.tech.core.misc.cryptography.model.PGPSubKeyType
import ai.tech.core.misc.cryptography.model.PGPUserId
import ai.tech.core.misc.cryptography.model.PGPVerifiedResult
import platform.JavaScriptCore.*
import platform.Foundation.*

private val openPgpJsContext = JSContext().apply {
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
    val userIDsJsArray = JSValue .makeArray(openPgpJsContext, userIDs.map { it.toJsUserId() }.toTypedArray(), null)
    val jsScript = """
        (async function() {
            const options = {
                userIDs: $userIDsJsArray,
                curve: "${key.type}", // ECC by default
                expirationTime: ${if (expireDate > 0) expireDate else "null"},
                passphrase: ${password?.let { "'$it'" } ?: "null"},
                format: ${if (armored) "'armored'" else "'binary'"}
            };
            const keyPair = await openpgp.generateKey(options);
            return ${if (armored) "keyPair.armor()" else "keyPair"};
        })();
    """.trimIndent()
    val result = openPgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpChangeKeyPassword(
    oldPasswords: List<String>,
    password: String?,
    armored: Boolean,
): ByteArray {
    val oldPassphrases = oldPasswords.joinToString(",") { "'$it'" }
    val jsScript = """
        (async function() {
            const privateKey = await openpgp.readPrivateKey({ armoredKey: ${decodeToString()} });
            const newKey = await openpgp.changeKeyPassword({
                privateKey: privateKey,
                passphrase: ${password?.let { "'$it'" } ?: "null"},
                format: ${if (armored) "'armored'" else "'binary'"}
            });
            return ${if (armored) "newKey.armor()" else "newKey"};
        })();
    """.trimIndent()
    val result = openPgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpPublicKey(armored: Boolean): ByteArray {
    val jsScript = """
        (async function() {
            const key = await openpgp.readKey({ armoredKey: ${decodeToString()} });
            return key.toPublic().${if (armored) "armor()" else "toByteArray()"};
        })();
    """.trimIndent()
    val result = openPgpJsContext.evaluateScript(jsScript)
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
    val result = openPgpJsContext.evaluateScript(jsScript)
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
    val result = openPgpJsContext.evaluateScript(jsScript)

    // Extract values from the JS object
    val metadataJsValue = result!!.toObject()
    return PGPKeyMetadata(
        keyID = metadataJsValue?.getProperty("keyID")?.toString() ?: "",
        creationTime = metadataJsValue?.getProperty("creationTime")?.toString()?.toLongOrNull() ?: 0L,
        expirationTime = metadataJsValue?.getProperty("expirationTime")?.toString()?.toLongOrNull(),
        algorithm = metadataJsValue?.getProperty("algorithm")?.toString() ?: "",
        bitSize = metadataJsValue?.getProperty("bitSize")?.toString()?.toIntOrNull() ?: 0,
        fingerprint = metadataJsValue?.getProperty("fingerprint")?.toString() ?: "",
        isPublicKey = metadataJsValue?.getProperty("isPublicKey")?.toBoolean() ?: false,
        userIDs = metadataJsValue?.getProperty("userIDs")?.toArray()?.map { it.toString() } ?: emptyList(),
    )
}

public actual suspend fun ByteArray.pgpKeyArmor(): ByteArray {
    val jsScript = """
        (async function() {
            const key = await openpgp.readKey({ armoredKey: "${decodeToString()}" });
            return key.armor();
        })();
    """.trimIndent()
    val result = openPgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpKeyDearmor(): ByteArray {
    val jsScript = """
        (async function() {
            const key = await openpgp.readKey({ armoredKey: "${decodeToString()}" });
            return key.toByteArray();
        })();
    """.trimIndent()
    val result = openPgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpRevokeKey(
    vararg passwords: String,
    armored: Boolean,
): ByteArray {
    val passwordsJsArray = JSValue.makeArray(openPgpJsContext, passwords.map { "'$it'" }.toTypedArray(), null)
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
    val result = openPgpJsContext.evaluateScript(jsScript)
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
        openPgpJsContext
        val encryptionKeysJsArray = , encryptionKeys.map { it.decodeToString() }.toTypedArray(), null)
    val signingKeysJsArray = JSValue.makeArray(
        openPgpJsContext,
        signingKeys?.map { it.decodeToString() }?.toTypedArray()
            ?: arrayOf(),
        null,
    )
    val passwordsJsArray = JSValue.makeArray(openPgpJsContext, passwords?.toTypedArray() ?: arrayOf(), null)
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
    val result = openPgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpDecrypt(
    decryptionKeys: List<ByteArray>,
    decryptionKeysPasswords: List<String>?,
    verificationKeys: List<ByteArray>?,
    passwords: List<String>?,
): PGPVerifiedResult {
    val decryptionKeysJsArray = JSValue.makeArray(openPgpJsContext, decryptionKeys.map { it.decodeToString() }.toTypedArray(), null)
    val verificationKeysJsArray = JSValue.makeArray(
        openPgpJsContext,
        verificationKeys?.map { it.decodeToString() }?.toTypedArray()
            ?: arrayOf(),
        null,
    )
    val passwordsJsArray = JSValue.makeArray(openPgpJsContext, passwords?.toTypedArray() ?: arrayOf(), null)
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
    val result = openPgpJsContext.evaluateScript(jsScript)
    return PGPVerifiedResult(result.toString().encodeToByteArray())
}

public actual suspend fun ByteArray.pgpSign(
    signingKeys: List<ByteArray>,
    signingKeysPasswords: List<String>?,
    mode: PGPSignMode,
    detached: Boolean,
    armored: Boolean,
): ByteArray {
    val signingKeysJsArray = JSValue.makeArray(openPgpJsContext, signingKeys.map { it.decodeToString() }.toTypedArray(), null)
    val passwordsJsArray = JSValue.makeArray(openPgpJsContext, signingKeysPasswords?.toTypedArray() ?: arrayOf(), null)
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
    val result = openPgpJsContext.evaluateScript(jsScript)
    return result.toString().encodeToByteArray()
}

public actual suspend fun ByteArray.pgpVerify(
    verificationKeys: List<ByteArray>,
    mode: PGPSignMode,
    signatures: List<ByteArray>?
): PGPVerifiedResult {
    val verificationKeysJsArray = JSValue.makeArray(openPgpJsContext, verificationKeys.map { it.decodeToString() }.toTypedArray(), null)
    val signaturesJsArray = JSValue.makeArray(
        openPgpJsContext,
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
    val result = openPgpJsContext.evaluateScript(jsScript)
    return PGPVerifiedResult(result.toString().encodeToByteArray())
}
