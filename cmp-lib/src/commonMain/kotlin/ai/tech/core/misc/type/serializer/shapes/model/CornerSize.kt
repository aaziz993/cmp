package ai.tech.core.misc.type.serializer.shapes.model

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

@Serializable
public data class CornerSize(
    val type: CornerSizeType,
    val size: Float
) : CornerSize {

    override fun toPx(shapeSize: Size, density: Density): Float = with(density) {
        when (type) {
            CornerSizeType.DP -> size.dp.toPx()
            CornerSizeType.PX -> size
            CornerSizeType.PERCENT -> shapeSize.minDimension * (size / 100f)
        }
    }
}
