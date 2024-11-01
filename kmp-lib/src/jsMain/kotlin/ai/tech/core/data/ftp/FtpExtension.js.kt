package ai.tech.core.data.ftp

import ai.tech.core.data.ftp.model.FtpClientConfig
import ai.tech.core.data.ftp.model.FtpHost

public actual fun createFtpClient(
    host: FtpHost,
    block: FtpClientConfig.() -> Unit,
): AbstractFtpClient = throw NotImplementedError()
