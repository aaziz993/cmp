package ai.tech.core.misc.network.ftp.client

import ai.tech.core.misc.network.ftp.client.model.FtpClientConfig
import ai.tech.core.misc.network.ftp.client.model.FtpHost
import ai.tech.core.misc.network.ftp.client.model.FtpScheme
import javax.net.ssl.TrustManager
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPSClient

public actual fun createFtpClient(
    host: FtpHost,
    block: FtpClientConfig.() -> Unit,
): AbstractFtpClient =
    FtpClientConfig().apply(block).let { cfg ->
        when (host.scheme) {
            FtpScheme.FTP -> FtpClientImpl(FTPClient().apply { autodetectUTF8 = true }, host, cfg)
            FtpScheme.FTPS ->
                FtpClientImpl(
                    FTPSClient().apply {
                        autodetectUTF8 = true
                        cfg.trustManager?.let { trustManager = it as TrustManager }
                    },
                    host,
                    cfg,
                )

            FtpScheme.SFTP -> SFtpClient(host, cfg)
        }
    }
