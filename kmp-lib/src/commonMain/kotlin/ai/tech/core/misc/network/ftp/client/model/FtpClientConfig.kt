package ai.tech.core.misc.network.ftp.client.model

public data class FtpClientConfig(
    public var hostKeyVerifierFingerprint: String? = null,
    public var privateKey: String? = null,
    public val trustManager: FtpClientTrustManager? = null
)
