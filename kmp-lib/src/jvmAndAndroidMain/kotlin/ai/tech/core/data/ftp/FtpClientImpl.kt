package ai.tech.core.data.ftp

import ai.tech.core.data.filesystem.model.path.PathMetadata
import ai.tech.core.data.filesystem.model.path.PathType
import ai.tech.core.data.ftp.model.FtpClientConfig
import ai.tech.core.data.ftp.model.FtpHost
import ai.tech.core.misc.type.multiple.asInputStream
import ai.tech.core.misc.type.multiple.flatMap
import ai.tech.core.misc.type.multiple.iterator
import ai.tech.core.misc.type.multiple.model.ClosableAbstractIterator
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPCmd
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import java.io.IOException

public class FtpClientImpl(
    private val client: FTPClient,
    host: FtpHost,
    config: FtpClientConfig,
) : AbstractFtpClient(host, config) {
    override var implicit: Boolean = false

    override var utf8: Boolean = false
        set(value) {
            if (value) client.controlEncoding = "UTF-8"
            field = value
        }

    override var passive: Boolean = false
        set(value) {
            if (value) {
                client.enterLocalPassiveMode()
            } else {
                client.enterLocalActiveMode()
            }
            field = value
        }

    override val isConnected: Boolean
        get() = client.isConnected

    override var privateData: Boolean = false

    private var supportsMlsCommands = false

    override fun connect() {
        with(host) {
            client.connect(host, port)
        }

        if (!FTPReply.isPositiveCompletion(client.replyCode)) {
            client.disconnect()
            throw IOException("Exception in connecting to FTP Server")
        }
    }

    override fun login() {
        with(host) {
            client.login(username, password)
        }
        client.setFileType(FTP.BINARY_FILE_TYPE)
        supportsMlsCommands = client.hasFeature(FTPCmd.MLST)
    }

    override fun pathMetadata(path: String): PathMetadata =
        if (supportsMlsCommands) {
            client.mlistFile(path).toPathMetadata()
        } else {
            throw IllegalStateException("Ftp server does not support MLST command.")
        }

    override fun pathIterator(path: String?): Iterator<PathMetadata> =
        (
                path?.let {
                    (if (supportsMlsCommands) client.mlistDir(it) else client.listFiles(it))
                } ?: (if (supportsMlsCommands) client.mlistDir() else client.listFiles())
                ).map(FTPFile::toPathMetadata).iterator()

    override fun createDirectory(path: String): Boolean = client.makeDirectory(path)

    override fun createSymlink(
        linkPath: String,
        targetPath: String,
    ): Boolean {
        // FTP does not support symbolic links, so this method may not be applicable.
        throw UnsupportedOperationException("FTP does not support symbolic links.")
    }

    override fun move(
        fromPath: String,
        toPath: String,
    ): Boolean = client.rename(fromPath, toPath)

    override fun copyFile(
        fromPath: String,
        toPath: String,
    ): Boolean = client.storeFile(toPath, client.retrieveFileStream(fromPath))

    override fun delete(path: String): Boolean {
        try {
            if (client.deleteFile(path)) {
                return true
            }
        } catch (_: Throwable) {
        }
        try {
            if (client.removeDirectory(path)) {
                return true
            }
        } catch (_: Throwable) {
        }
        return false
    }

    override fun readFile(
        path: String,
        bufferSize: Int,
    ): ClosableAbstractIterator<ByteArray> = client.retrieveFileStream(path).iterator(bufferSize) {
        client.completePendingCommand()
    }

    override fun writeFile(
        data: Iterator<ByteArray>,
        path: String,
        append: Boolean,
    ): Boolean =
        if (append) {
            client.appendFile(path, data.flatMap { it.iterator() }.asInputStream())
            true
        } else {
            client.storeFile(path, data.flatMap { it.iterator() }.asInputStream())
        }.also {
            client.completePendingCommand()
        }

    override fun close() {
        client.logout()
        client.disconnect()
    }
}

private fun FTPFile.toPathMetadata(): PathMetadata =
    PathMetadata(
        name,
        if (isFile) {
            PathType.REGULAR_FILE
        } else if (isDirectory) {
            PathType.DIRECTORY
        } else if (
            isSymbolicLink
        ) {
            PathType.SYMBOLIC_LINK
        } else {
            PathType.OTHER
        },
        null,
        null,
        timestampInstant.toEpochMilli(),
        size,
        emptyMap(),
    )
