package ai.tech.core.data.ftp.model

public open class FtpHost(
    public val scheme: FtpScheme,
    public val username: String = scheme.defaultUsername,
    public val password: String = "",
    public val host: String,
    public val port: Int = scheme.defaultPort,
) {
    override fun toString(): String {
        val sb = StringBuilder("${scheme.name.lowercase()}://")

        if (username != scheme.defaultUsername) {
            sb.append(username)
            if (password.isNotEmpty()) {
                sb.append(":$password")
            }
            sb.append("@")
        }
        sb.append(host)

        if (port != scheme.defaultPort) {
            sb.append(":$port")
        }

        return sb.toString()
    }

    companion object {
        operator fun invoke(url: String): FtpHost = FtpUrl(url)
    }
}
