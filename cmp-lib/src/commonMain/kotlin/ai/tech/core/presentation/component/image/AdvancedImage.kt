package ai.tech.core.presentation.component.image

import ai.tech.core.misc.type.multiple.toImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale

@Composable
public fun AdvancedImage(
    source: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
): Unit = when (source) {
    is ByteArray -> Image(
        source.toImageBitmap(),
        contentDescription,
        modifier,
        alignment,
        contentScale,
        alpha,
        colorFilter,
    )

    is Painter -> Image(
        source,
        contentDescription,
        modifier,
        alignment,
        contentScale,
        alpha,
        colorFilter,
    )

    is ImageBitmap -> Image(
        source,
        contentDescription,
        modifier,
        alignment,
        contentScale,
        alpha,
        colorFilter,
    )

    is ImageVector -> Image(
        source,
        contentDescription,
        modifier,
        alignment,
        contentScale,
        alpha,
        colorFilter,
    )

    else -> throw IllegalArgumentException("Unsupported image source type: ${source::class.simpleName}")
}

