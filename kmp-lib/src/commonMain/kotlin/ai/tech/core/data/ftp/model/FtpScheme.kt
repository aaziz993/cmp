package ai.tech.core.data.ftp.model

public enum class FtpScheme(
    public val defaultUsername: String,
    public val defaultPort: Int,
) {
    FTP("anonymous", 21),
    FTPS("anonymous", 21),
    SFTP("anonymous", 22),
}
