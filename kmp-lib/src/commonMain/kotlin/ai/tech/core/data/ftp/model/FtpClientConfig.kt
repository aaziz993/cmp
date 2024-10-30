package ai.tech.core.data.ftp.model

public data class FtpClientConfig(
    public var hostKeyVerifierFingerprint: String? = null,
    public var privateKey: String? = null,
    public val trustManager: FtpClientTrustManager? = null
)