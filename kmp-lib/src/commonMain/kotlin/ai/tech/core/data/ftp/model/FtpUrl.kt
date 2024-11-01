package ai.tech.core.data.ftp.model

public class FtpUrl(
    scheme: FtpScheme,
    username: String = scheme.defaultUsername,
    password: String = "",
    host: String,
    port: Int = scheme.defaultPort,
    public val path: String = "/",
) : FtpHost(scheme, username, password, host, port) {

    override fun toString(): String = "${super.toString()}$path"

    public companion object {

        public operator fun invoke(url: String): FtpUrl {
            val matchResult =
                ftpUR.matchEntire(url)
                    ?: throw IllegalArgumentException("Invalid FTP url: $url")

            val (scheme, username, password, host, port, path) = matchResult.destructured
            val ftpScheme = FtpScheme.valueOf(scheme.uppercase())
            return FtpUrl(
                scheme = ftpScheme,
                username = username.ifBlank { ftpScheme.defaultUsername },
                password = if (username == ftpScheme.defaultUsername) "" else password.ifBlank { "" },
                host = host,
                port = if (port.isBlank()) ftpScheme.defaultPort else port.toInt(),
                path = path.ifBlank { "/" },
            )
        }
    }
}

public val ftpPR: Regex = "^(ftp|ftps|sftp)://.*".toRegex(RegexOption.IGNORE_CASE)
public val ftpUR: Regex = """^(ftp|ftps|sftp)://(?:(\w+)(?::(\w+))?@)?([^:/]+)(?::(\d+))?(/.*)?$""".toRegex(RegexOption.IGNORE_CASE)

public val String.isFtpUrl: Boolean
    get() = matches(ftpPR)

public val String.isValidFtpUrl: Boolean
    get() = matches(ftpUR)
