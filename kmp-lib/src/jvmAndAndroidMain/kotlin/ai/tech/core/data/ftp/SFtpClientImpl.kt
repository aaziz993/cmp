package ai.tech.core.data.ftp

import ai.tech.core.data.filesystem.model.path.PathMetadata
import ai.tech.core.data.filesystem.model.path.PathType
import ai.tech.core.data.ftp.model.FtpClientConfig
import ai.tech.core.data.ftp.model.FtpHost
import ai.tech.core.misc.type.multiple.asInputStream
import ai.tech.core.misc.type.multiple.flatMap
import ai.tech.core.misc.type.multiple.iterator
import ai.tech.core.misc.type.multiple.model.ClosableAbstractIterator
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.LoggerFactory
import net.schmizz.sshj.common.StreamCopier
import net.schmizz.sshj.sftp.FileAttributes
import net.schmizz.sshj.sftp.FileMode
import net.schmizz.sshj.sftp.OpenMode
import net.schmizz.sshj.sftp.RemoteFile
import net.schmizz.sshj.sftp.RemoteFile.ReadAheadRemoteFileInputStream
import net.schmizz.sshj.sftp.RemoteFile.RemoteFileOutputStream
import net.schmizz.sshj.sftp.SFTPClient
import java.io.IOException
import java.io.InputStream
import java.util.*

public open class SFtpClient(
    host: FtpHost,
    config: FtpClientConfig,
) : AbstractFtpClient(host, config) {

    private val loggerFactory: LoggerFactory = LoggerFactory.DEFAULT

    private val client = SSHClient()
    private val sftp: SFTPClient = client.newSFTPClient()

    override var implicit: Boolean = false
    override var utf8: Boolean = false
    override var passive: Boolean = false

    override val isConnected: Boolean
        get() = client.isConnected && client.isAuthenticated

    override var privateData: Boolean = false

    override fun connect() {
        with(config) {
            hostKeyVerifierFingerprint?.let { client.addHostKeyVerifier(it) }
            with(host) {
                client.connect(host, port)
            }
        }
    }

    override fun login() {
        with(config) {
            with(host) {
                if (privateKey == null) {
                    client.authPassword(username, password)
                }
                else {
                    val kp = client.loadKeys(privateKey, password)
                    client.authPublickey(username, kp)
                }
            }
        }
    }

    override fun pathMetadata(path: String): PathMetadata = sftp.lstat(path).toPathMetadata(path)

    override fun pathIterator(path: String?): Iterator<PathMetadata> =
        sftp
            .ls(path ?: ".")
            .map { it.attributes.toPathMetadata(it.path) }
            .iterator()

    public override fun createDirectory(path: String): Boolean {
        sftp.mkdir(path)
        return true
    }

    override fun createSymlink(
        linkPath: String,
        targetPath: String,
    ): Boolean {
        sftp.symlink(linkPath, targetPath)
        return true
    }

    override fun move(
        fromPath: String,
        toPath: String,
    ): Boolean {
        sftp.rename(fromPath, toPath)
        return true
    }

    override fun copyFile(
        fromPath: String,
        toPath: String,
    ): Boolean {
        val copied: Boolean
        val remoteFile = sftp.open(fromPath)
        try {
            val remoteInputStream: ReadAheadRemoteFileInputStream = remoteFile.ReadAheadRemoteFileInputStream(16)
            try {
                copied = copy(remoteInputStream, toPath)
            }
            finally {
                remoteInputStream.close()
            }
        }
        finally {
            remoteFile.close()
        }
        return copied
    }

    public override fun delete(path: String): Boolean {
        var deleted = false
        try {
            sftp.rm(path)
            deleted = true
        }
        catch (_: Throwable) {
        }
        try {
            sftp.rmdir(path)
            deleted = true
        }
        catch (_: Throwable) {
        }
        return deleted
    }

    override fun readFile(
        path: String,
        bufferSize: Int,
    ): ClosableAbstractIterator<ByteArray> {
        val rf = sftp.open(path)
        try {
            return rf.ReadAheadRemoteFileInputStream(16).iterator(bufferSize = bufferSize)
        }
        catch (e: Throwable) {
            rf.close()
            throw e
        }
    }

    override fun writeFile(
        data: Iterator<ByteArray>,
        path: String,
        append: Boolean,
    ): Boolean = copy(data.flatMap { it.iterator() }.asInputStream(), path, append)

    override fun close() {
        sftp.close()
        client.disconnect()
    }

    private fun copy(
        fromInputStream: InputStream,
        toPath: String,
        append: Boolean = false,
    ): Boolean {
        var rf: RemoteFile? = null

        var rfos: RemoteFileOutputStream? = null

        try {
            rf = sftp.open(toPath, toFileModes(append))

            rfos = rf.RemoteFileOutputStream(rf.fetchAttributes().size, 16)

            StreamCopier(fromInputStream, rfos, loggerFactory)
                .bufSize(
                    sftp.sftpEngine.subsystem.remoteMaxPacketSize - rf.outgoingPacketOverhead,
                ).keepFlushing(false)
                .copy()
        }
        catch (_: Throwable) {
            return false
        }
        finally {
            if (rf != null) {
                try {
                    rf.close()
                }
                catch (_: IOException) {
                }
            }
            if (rfos != null) {
                try {
                    rfos.close()
                }
                catch (_: IOException) {
                }
            }
        }
        return true
    }
}

internal fun toFileModes(append: Boolean): EnumSet<OpenMode> =
    if (append) {
        EnumSet.of(OpenMode.WRITE, OpenMode.APPEND)
    }
    else {
        EnumSet.of(OpenMode.WRITE, OpenMode.CREAT, OpenMode.TRUNC)
    }

internal fun FileAttributes.toPathMetadata(path: String): PathMetadata =
    PathMetadata(
        path,
        if (type == FileMode.Type.REGULAR) {
            PathType.REGULAR_FILE
        }
        else if (type == FileMode.Type.DIRECTORY) {
            PathType.DIRECTORY
        }
        else if (type == FileMode.Type.SYMLINK) {
            PathType.SYMBOLIC_LINK
        }
        else {
            PathType.OTHER
        },
        null,
        atime * 1000L,
        mtime * 1000L,
        size,
        emptyMap(),
    )
