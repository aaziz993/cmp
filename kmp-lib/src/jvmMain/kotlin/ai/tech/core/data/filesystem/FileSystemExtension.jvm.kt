package ai.tech.core.data.filesystem

import ai.tech.core.misc.type.multiple.decode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

private val clipboard: Clipboard
    get() = Toolkit.getDefaultToolkit().systemClipboard

public actual suspend fun String.toClipboard(): Unit =
    withContext(Dispatchers.IO) {
        clipboard.setContents(StringSelection(this@toClipboard), null)
    }

public actual suspend fun fromClipboard(): String? =
    withContext(Dispatchers.IO) {
        try {
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                clipboard.getData(DataFlavor.stringFlavor) as String
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

public fun readResourceBytes(path: String): ByteArray? =
    Thread.currentThread().contextClassLoader.getResourceAsStream(path)?.readAllBytes()

public fun readResourceText(path: String): String? = readResourceBytes(path)?.decode()
