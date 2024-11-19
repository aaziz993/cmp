package ai.tech.core.presentation.component.pager

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
public fun AdvancedPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    horizontal: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageSize: PageSize = PageSize.Fill,
    beyondBoundsPageCount: Int = 1,
    pageSpacing: Dp = 0.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    flingBehavior: TargetedFlingBehavior = PagerDefaults.flingBehavior(state = state),
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    autoScroll: Boolean = true,
    autoScrollDuration: Long = 1000L,
    animationSpec: AnimationSpec<Float> = tween(
        durationMillis = 800
    ),
    indicator: (@Composable BoxScope.() -> Unit)? = {
        val scope = rememberCoroutineScope()
        HorizontalPagerIndicator(
            state = state,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomCenter),
            onIndicatorClick = {
                scope.launch {
                    state.animateScrollToPage(it, animationSpec = animationSpec)
                }
            }
        )
    },
    previousButtonIcon: ImageVector? = null,
    nextButtonIcon: ImageVector? = null,
    label: (@Composable BoxScope.(Int) -> Unit)? = null,
    item: @Composable PagerScope.(Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val nextPage: () -> Int = {
        val nextPage = (state.currentPage + 1).mod(state.pageCount)
        coroutineScope.launch {
            state.animateScrollToPage(
                nextPage,
                animationSpec = animationSpec
            )
        }
        nextPage
    }

    val previousPage: () -> Int = {
        var previousPage = (state.currentPage - 1)
        if (previousPage < 0) {
            previousPage = max(state.pageCount - 1, 0)
        }
        coroutineScope.launch {
            state.animateScrollToPage(
                previousPage,
                animationSpec = animationSpec
            )
        }
        previousPage
    }

    if (autoScroll) {
        val isDragged by state.interactionSource.collectIsDraggedAsState()
        if (!isDragged) {
            with(state) {
                if (pageCount > 0) {
                    var currentPageKey by remember { mutableIntStateOf(0) }
                    LaunchedEffect(currentPageKey) {
                        launch {
                            delay(timeMillis = autoScrollDuration)
                            currentPageKey = nextPage()
                        }
                    }
                }
            }
        }
    }

    Box(contentAlignment = Alignment.Center) {
        if (horizontal) {
            HorizontalPager(
                state,
                modifier,
                contentPadding,
                pageSize,
                beyondBoundsPageCount,
                pageSpacing,
                verticalAlignment,
                flingBehavior,
                userScrollEnabled,
                reverseLayout,
            ) { page: Int ->
                item(page)
            }
            previousButtonIcon?.let {
                IconButton({
                    previousPage()
                }, Modifier.align(Alignment.CenterStart)) {
                    Icon(it, null)
                }
            }

            nextButtonIcon?.let {
                IconButton({
                    nextPage()
                }, Modifier.align(Alignment.CenterEnd)) {
                    Icon(it, null)
                }
            }
        } else {
            VerticalPager(
                state,
                modifier,
                contentPadding,
                pageSize,
                beyondBoundsPageCount,
                pageSpacing,
                horizontalAlignment,
                flingBehavior,
                userScrollEnabled,
                reverseLayout,
            ) { page ->
                item(page)
            }
            previousButtonIcon?.let {
                IconButton({
                    previousPage()
                }, Modifier.align(Alignment.TopCenter)) {
                    Icon(it, null)
                }
            }

            nextButtonIcon?.let {
                IconButton({
                    nextPage()
                }, Modifier.align(Alignment.BottomCenter)) {
                    Icon(it, null)
                }
            }
        }
        indicator?.invoke(this)
        label?.invoke(this, state.currentPage)
    }
}

public fun Modifier.pagerTransition(
    page: Int,
    pagerState: PagerState
): Modifier = graphicsLayer {
    val pageOffset =
        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

    val transformation = lerp(
        start = Shadow(blurRadius = 0.8f),
        stop = Shadow(blurRadius = 1f),
        fraction = 1f - pageOffset.coerceIn(
            0f,
            1f
        )
    )
    alpha = transformation.blurRadius
    scaleY = transformation.blurRadius
}
