@file:JsModule("openpgp")

package ai.tech.core.misc.cryptography

import js.date.Date
import js.promise.Promise
import js.typedarrays.Uint8Array
import kotlinx.js.JsPlainObject

@JsPlainObject
internal external interface UserID {
    var name: String?
    var email: String?
    var comment: String?
}

@JsPlainObject
internal external interface Config {
    var preferredHashAlgorithm: Int?
    var preferredSymmetricAlgorithm: Int?
    var preferredCompressionAlgorithm: Int?
}

@JsPlainObject
internal external interface SubkeyOptions {
    var type: String?
    var curve: String?
    var rsaBits: Double?
    var date: Date?
    var sign: Boolean?
    var config: Config?
}

@JsPlainObject
internal external interface GenerateKeyOptions {
    var userIDs: Array<UserID>?
    var passphrase: String?
    var type: String
    var curve: String?
    var rsaBits: Double?
    var keyExpirationTime: Double?
    var date: Date?
    var subkeys: Array<SubkeyOptions>?
    var format: String
    var config: Config?
}

internal external fun generateKey(options: GenerateKeyOptions): Promise<dynamic>

internal open external class Key {
    fun write(): Uint8Array

    fun armor(config: Config? = definedExternally): String

    fun getExpirationTime(
        userID: UserID = definedExternally,
        config: Config = definedExternally,
    ): Promise<Any? /* Date | typeof Infinity | null */>

    fun getUserIDs(): Array<String>

    fun toPublic(): Key

    fun getFingerprint(): String

    fun getCreationTime(): Date
}

internal external class PrivateKey : Key {
    fun isDecrypted(): Boolean
}

@JsPlainObject
internal external interface ReadKeyOptions {
    var armoredKey: String?
    var binaryKey: Uint8Array?
}

@JsPlainObject
internal external interface ReadKeysOptions {
    var armoredKeys: String?
    var binaryKeys: Uint8Array?
}

internal external fun readKey(options: ReadKeyOptions): Promise<Key>

internal external fun readKeys(options: ReadKeysOptions): Promise<Array<Key>>

internal external fun readPrivateKey(options: ReadKeyOptions): Promise<PrivateKey>

internal external fun readPrivateKeys(options: ReadKeysOptions): Promise<Array<PrivateKey>>

@JsPlainObject
internal external interface EncryptDecryptKeyOptions {
    var privateKey: PrivateKey
    var passphrase: Array<String>
}

internal external fun encryptKey(options: EncryptDecryptKeyOptions): Promise<PrivateKey>

internal external fun decryptKey(options: EncryptDecryptKeyOptions): Promise<PrivateKey>

@JsPlainObject
internal external interface RevokeKeyOptions {
    var key: PrivateKey
    var format: String
}

internal external fun revokeKey(options: RevokeKeyOptions): Promise<dynamic>

internal external interface Message

@JsPlainObject
internal external interface CreateMessageOptions {
    var text: Any
    var binary: Any
}

internal external fun createMessage(options: CreateMessageOptions): Promise<Message>

internal external interface CleartextMessage

@JsPlainObject
internal external interface CreateCleartextMessageOptions {
    var text: String
}

internal external fun createCleartextMessage(options: CreateCleartextMessageOptions): Promise<CleartextMessage>

@JsPlainObject
internal external interface EncryptOptions {
    var message: Message
    var encryptionKeys: Array<Key>?
    var signingKeys: Array<PrivateKey>?
    var passwords: Array<String>?
    var format: String?
}

internal external fun encrypt(options: EncryptOptions): Promise<dynamic>

@JsPlainObject
internal external interface ReadMessageOptions {
    var armoredMessage: String
    var binaryMessage: Any
}

internal external fun readMessage(options: ReadMessageOptions): Promise<Message>

internal external class KeyID {
    fun toHex(): String
}

@JsPlainObject
internal external interface VerificationResult {
    var keyID: KeyID
    var verified: Promise<Boolean>
    var signature: Promise<Signature>
}

@JsPlainObject
internal external interface DecryptVerifyMessageResult {
    var data: dynamic
    var signatures: Array<VerificationResult>
}

internal external class Signature

@JsPlainObject
internal external interface DecryptOptions {
    var message: Message
    var decryptionKeys: Array<PrivateKey>
    var passwords: Array<String>?
    var verificationKeys: Array<Key>?
    var format: String?
    var signature: Signature?
}

internal external fun decrypt(options: DecryptOptions): Promise<DecryptVerifyMessageResult>

@JsPlainObject
internal external interface SignOptions {
    var message: Any
    var signingKeys: Array<PrivateKey>
    var format: String?
    var detached: Boolean?
}

internal external fun sign(options: SignOptions): Promise<Any>

@JsPlainObject
internal external interface VerifyOptions {
    var message: Any
    var verificationKeys: Array<Key>
    var format: String?
    var signature: Signature?
}

internal external fun verify(options: VerifyOptions): Promise<DecryptVerifyMessageResult>

@JsPlainObject
internal external interface ReadSignatureOptions {
    var armoredSignature: String?
    var binarySignature: Uint8Array?
}

internal external fun readSignature(options: ReadSignatureOptions): Promise<Signature>
