package ai.tech.core.data.ftp

import ai.tech.core.DEFAULT_BUFFER_SIZE
import ai.tech.core.data.ftp.model.FtpClientConfig
import ai.tech.core.data.ftp.model.FtpHost

@OptIn(ExperimentalStdlibApi::class)
public abstract class AbstractFtpClient(
    protected val host: FtpHost,
    protected val config: FtpClientConfig,
) : AutoCloseable {
    public abstract var implicit: Boolean
    public abstract var utf8: Boolean
    public abstract var passive: Boolean
    public abstract val isConnected: Boolean
    public abstract var privateData: Boolean
    public abstract fun connect()
    public abstract fun login()
    public abstract fun pathMetadata(path: String): PathMetadata

    protected abstract fun pathIterator(path: String? = null): Iterator<PathMetadata>
    public fun pathTraverser(
        path: String,
        depth: Int = 0,
        followSymlinks: Boolean = false,
        depthFirst: Boolean = false,
    ): Iterator<PathMetadata> = pathIterator(path).traverser(depth, followSymlinks, depthFirst)

    public abstract fun createDirectory(path: String): Boolean
    public abstract fun createSymlink(
        linkPath: String,
        targetPath: String,
    ): Boolean

    public abstract fun move(
        fromPath: String,
        toPath: String,
    ): Boolean

    public abstract fun copyFile(
        fromPath: String,
        toPath: String,
    ): Boolean

    public fun copyDirectory(
        fromPath: String,
        toPath: String,
    ): Unit =
        pathTraverser(fromPath, depth = -1, depthFirst = true).forEach {
            val path = it.path.pathResolveToRoot(fromPath, toPath)
            if (it.isDirectory) {
                createDirectory(path)
            } else if (it.isRegularFile) {
                copyFile(it.path, path)
            }
        }

    public fun copy(
        fromPath: String,
        toPath: String,
    ): Unit =
        pathMetadata(fromPath).let {
            if (it.isRegularFile) {
                copyFile(fromPath, toPath)
            } else if (it.isDirectory) {
                copyDirectory(fromPath, toPath)
            }
        }

    public abstract fun delete(path: String): Boolean

    public abstract fun readFile(
        path: String,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
    ): ClosableAbstractIterator<ByteArray>

    public fun readBytes(
        path: String,
        bufferSize: Int = DEFAULT_BUFFER_SIZE
    ): ByteArray = readFile(path, bufferSize).flatMap(ByteArray::iterator).toList()
        .toByteArray()

    public fun copyFileToLocal(
        remotePath: String,
        localPath: String,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        ifNotExists: Boolean = false
    ): Unit =
        localPath.localPathWrite(readFile(remotePath, bufferSize), ifNotExists)

    public abstract fun writeFile(
        data: Iterator<ByteArray>,
        path: String,
        append: Boolean = false,
    ): Boolean
}