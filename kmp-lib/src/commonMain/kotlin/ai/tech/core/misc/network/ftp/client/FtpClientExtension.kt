package ai.tech.core.misc.network.ftp.client

import ai.tech.core.misc.network.ftp.client.model.FtpClientConfig
import ai.tech.core.misc.network.ftp.client.model.FtpHost

public expect fun createFtpClient(
    host: FtpHost,
    block: FtpClientConfig.() -> Unit = {},
): AbstractFtpClient
