package ai.tech.core.misc.type.multiple

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

public actual fun ByteArray.toImageBitmap(): ImageBitmap = Image.makeFromEncoded(this).toComposeImageBitmap()
