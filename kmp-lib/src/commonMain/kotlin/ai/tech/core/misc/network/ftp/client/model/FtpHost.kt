package ai.tech.core.misc.network.ftp.client.model

import ai.tech.core.misc.network.ftp.client.model.FtpUrl.Companion.invoke

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

    public companion object {
        public operator fun invoke(url: String): FtpHost = FtpUrl(url)
    }
}
