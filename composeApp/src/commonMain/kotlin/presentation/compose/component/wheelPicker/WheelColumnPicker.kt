package presentation.compose.component.wheelPicker

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

// Ease-out fling curve: aggressive braking at high speeds, gentle at low speeds.
// Normalized so flingVelocityMultiplier keeps the same meaning at ~3000 px/s as it would linearly.
private const val FLING_CURVE_EXPONENT = 0.7f
private val FLING_NORM_FACTOR = 3000f.pow(1f - FLING_CURVE_EXPONENT)

@Composable
internal fun WheelColumnPicker(
    items: List<String>,
    initialIndex: Int,
    itemHeight: Dp,
    visibleItemCount: Int,
    enabled: Boolean,
    flingVelocityMultiplier: Float,
    onIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val safeInitialIndex = initialIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    val pickerHeight = itemHeight * visibleItemCount
    val contentPadding = itemHeight * ((visibleItemCount - 1) / 2)

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = safeInitialIndex)
    val snapFling = rememberSnapFlingBehavior(remember(listState) { SnapLayoutInfoProvider(listState) })
    val flingBehavior = remember(snapFling, flingVelocityMultiplier) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                val scaledV = sign(initialVelocity) *
                    abs(initialVelocity).pow(FLING_CURVE_EXPONENT) *
                    FLING_NORM_FACTOR *
                    flingVelocityMultiplier
                return with(snapFling) { performFling(scaledV) }
            }
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val snappedIndex = calculateSnappedIndex(listState)
            listState.scrollToItem(snappedIndex)
            onIndexChanged(snappedIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.height(pickerHeight),
        contentPadding = PaddingValues(vertical = contentPadding),
        flingBehavior = flingBehavior,
        userScrollEnabled = enabled,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(items.size) { index ->
            val (animatedAlpha, animatedRotationX) = calculateAnimatedAlphaAndRotationX(
                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
                pickerHeightPx = listState.layoutInfo.viewportSize.height.toFloat(),
                index = index,
                visibleItemCount = visibleItemCount,
            )

            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .wrapContentWidth()
                    .padding(horizontal = 12.dp)
                    .alpha(animatedAlpha)
                    .graphicsLayer { rotationX = animatedRotationX },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = items[index],
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

private fun calculateSnappedIndex(listState: androidx.compose.foundation.lazy.LazyListState): Int {
    val firstVisibleIndex = listState.firstVisibleItemIndex
    val scrollOffset = listState.firstVisibleItemScrollOffset
    val itemHeight = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: return firstVisibleIndex
    val totalItems = listState.layoutInfo.totalItemsCount
    return if (scrollOffset > itemHeight / 2 && firstVisibleIndex < totalItems - 1) {
        firstVisibleIndex + 1
    } else {
        firstVisibleIndex
    }
}

private fun calculateAnimatedAlphaAndRotationX(
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffset: Int,
    pickerHeightPx: Float,
    index: Int,
    visibleItemCount: Int,
): Pair<Float, Float> {
    val singleItemHeight = pickerHeightPx / visibleItemCount
    val distanceToCenterIndex = index - firstVisibleItemIndex
    val distanceToSnap = distanceToCenterIndex * singleItemHeight.toInt() - firstVisibleItemScrollOffset
    val distanceToSnapAbs = abs(distanceToSnap)

    val alpha = if (distanceToSnapAbs <= singleItemHeight) {
        1.2f - (distanceToSnapAbs / singleItemHeight)
    } else {
        0.2f
    }

    val rotationX = (-20f * (distanceToSnap / singleItemHeight)).takeUnless { it.isNaN() } ?: 0f

    return alpha to rotationX
}
