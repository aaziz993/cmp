package ai.tech.core.data.model

import android.graphics.Bitmap

internal val imageCompressionMap =
    mapOf(
        ImageCompression.PNG to Bitmap.CompressFormat.PNG,
        ImageCompression.JPEG to Bitmap.CompressFormat.JPEG,
    )
