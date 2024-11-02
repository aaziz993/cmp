package ai.tech.core.data.environment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val clipboardManager: ClipboardManager
    get() = appCtx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

public actual suspend fun String.toClipboard(): Unit =
    withContext(Dispatchers.IO) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("text", this@toClipboard))
    }

public actual suspend fun fromClipboard(): String? =
    withContext(Dispatchers.IO) {
        clipboardManager.primaryClip?.let {
            if (it.itemCount > 0) {
                it.getItemAt(0).text.toString()
            }
            else {
                null
            }
        }
    }
