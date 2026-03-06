package presentation.compose.component.wheelPicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sign

// Constant deceleration rate in px/s². Faster flings travel proportionally longer.
private const val DECEL_RATE = 2500f

// Speed (px/s) at which Phase 1 ends and the snap curve begins.
// Higher values = snap curve starts faster = shorter snap duration.
// 250px/s keeps snap duration roughly equal to the old coast+decel approach.
private const val SNAP_VELOCITY = 250f

// Fraction of SNAP_VELOCITY lost in the FIRST half of the snap curve's time.
// 0.8 means 80% of speed is gone by midpoint, leaving a gentle tail.
private const val SNAP_STEEP_FRACTION = 0.75f

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
    val flingBehavior = remember(listState, flingVelocityMultiplier) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                val scaledVelocity = initialVelocity * flingVelocityMultiplier
                if (abs(scaledVelocity) <= SNAP_VELOCITY) return 0f

                val itemSizePx = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size?.toFloat()
                    ?: return 0f

                val direction = sign(scaledVelocity)
                var velocity = scaledVelocity

                var prevNanos = 0L
                withFrameNanos { prevNanos = it }

                // Phase 1: Constant deceleration until snap velocity
                while (abs(velocity) > SNAP_VELOCITY) {
                    var currNanos = 0L
                    withFrameNanos { currNanos = it }
                    val dt = ((currNanos - prevNanos) / 1_000_000_000f).coerceAtMost(0.05f)
                    prevNanos = currNanos

                    val moved = scrollBy(velocity * dt)
                    if (moved == 0f && abs(velocity * dt) > 0.1f) break

                    velocity = if (direction > 0f)
                        maxOf(SNAP_VELOCITY, velocity - DECEL_RATE * dt)
                    else
                        minOf(-SNAP_VELOCITY, velocity + DECEL_RATE * dt)
                }

                // Phase 2: Smooth snap curve from SNAP_VELOCITY to 0.
                //
                // Velocity profile is piecewise-linear:
                //   first half of time  → lose SNAP_STEEP_FRACTION of SNAP_VELOCITY (steeper)
                //   second half of time → lose remaining fraction gently (lands softly)
                //
                // Integrating this profile gives a piecewise-quadratic easing.
                // We compute the total distance D to the nearest item boundary, derive the
                // curve duration from D, then animate exactly that distance via Animatable.
                val snapDStop = SNAP_VELOCITY * SNAP_VELOCITY / (2f * DECEL_RATE)
                val currentTotal = listState.firstVisibleItemIndex * itemSizePx +
                    listState.firstVisibleItemScrollOffset
                val naturalStop = currentTotal + direction * snapDStop
                val targetTotal = if (direction > 0f)
                    ceil(naturalStop.toDouble() / itemSizePx).toFloat() * itemSizePx
                else
                    floor(naturalStop.toDouble() / itemSizePx).toFloat() * itemSizePx

                val dTotal = direction * (targetTotal - currentTotal)
                if (dTotal < 0.5f) return 0f

                // Piecewise-quadratic easing derived from piecewise-linear velocity:
                //   f(p) for p ≤ 0.5 : 4p(1 - α·p) / (3 - 2α)
                //   f(p) for p > 0.5  : [(2-α) + 4(1-α)(p-0.5)(1-(p-0.5))] / (3 - 2α)
                // where α = SNAP_STEEP_FRACTION
                val α = SNAP_STEEP_FRACTION
                val denom = 3f - 2f * α
                val snapEasing = Easing { p ->
                    if (p <= 0.5f) {
                        4f * p * (1f - α * p) / denom
                    } else {
                        val q = p - 0.5f
                        ((2f - α) + 4f * (1f - α) * q * (1f - q)) / denom
                    }
                }
                // Duration: T = 4·D / (V0·(3-2α))  [from integrating the velocity profile]
                val snapDurationMs = (4f * dTotal / (SNAP_VELOCITY * denom) * 1000f)
                    .toInt().coerceIn(16, 2000)

                val scrollScope = this
                val posAnim = Animatable(0f)
                var lastAnimPos = 0f
                posAnim.animateTo(
                    targetValue = dTotal,
                    animationSpec = tween(snapDurationMs, easing = snapEasing),
                ) {
                    scrollScope.scrollBy(direction * (value - lastAnimPos))
                    lastAnimPos = value
                }

                return 0f
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
