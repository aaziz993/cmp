package ai.tech.core.data.environment

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
