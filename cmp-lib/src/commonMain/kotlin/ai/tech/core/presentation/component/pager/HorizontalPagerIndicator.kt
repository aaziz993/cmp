package ai.tech.core.presentation.component.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.math.sign

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun HorizontalPagerIndicator(
    state: PagerState,
    modifier: Modifier = Modifier,
    pageIndexMapping: (Int) -> Int = { it },
    activeColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    inactiveColor: Color = activeColor.copy(ContentAlpha.disabled),
    indicatorWidth: Dp = 8.dp,
    indicatorHeight: Dp = indicatorWidth,
    spacing: Dp = indicatorWidth,
    indicatorShape: Shape = CircleShape,
    onIndicatorClick: (Int) -> Unit,
) {

    val indicatorWidthPx = LocalDensity.current.run { indicatorWidth.roundToPx() }
    val spacingPx = LocalDensity.current.run { spacing.roundToPx() }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            repeat(state.pageCount) {
                Box(
                    modifier = Modifier
                        .size(width = indicatorWidth, height = indicatorHeight)
                        .clip(indicatorShape)
                        .background(color = inactiveColor)
                        .clickable { onIndicatorClick(it) }  //modified here
                )
            }
        }

        Box(
            Modifier
                .offset {
                    val position = pageIndexMapping(state.currentPage)
                    val offset = state.currentPageOffsetFraction
                    val next = pageIndexMapping(state.currentPage + offset.sign.toInt())
                    val scrollPosition = ((next - position) * offset.absoluteValue + position)
                        .coerceIn(
                            0f,
                            (state.pageCount - 1)
                                .coerceAtLeast(0)
                                .toFloat()
                        )

                    IntOffset(
                        x = ((spacingPx + indicatorWidthPx) * scrollPosition).toInt(),
                        y = 0
                    )
                }
                .size(width = indicatorWidth, height = indicatorHeight)
                .then(
                    if (state.pageCount > 0) Modifier.background(
                        color = activeColor,
                        shape = indicatorShape,
                    )
                    else Modifier
                )
        )
    }
}
