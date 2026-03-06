package presentation.compose.component.pager

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

class HorizontalCarouselScope {
    internal val pages = mutableListOf<@Composable () -> Unit>()

    fun page(content: @Composable () -> Unit) {
        pages.add(content)
    }
}

@Composable
fun HorizontalCarousel(
    selectedPage: Int,
    modifier: Modifier = Modifier,
    content: HorizontalCarouselScope.() -> Unit,
) {
    val pages = remember(content) { HorizontalCarouselScope().apply(content).pages }

    val slideOffset = remember { Animatable(0f) }
    var displayedPage by remember { mutableIntStateOf(selectedPage) }

    LaunchedEffect(selectedPage) {
        if (selectedPage == displayedPage) return@LaunchedEffect
        val direction = if (selectedPage > displayedPage) 1f else -1f
        slideOffset.animateTo(
            direction * -1f,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        )
        displayedPage = selectedPage
        slideOffset.snapTo(direction * 1f)
        slideOffset.animateTo(
            0f,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        )
    }

    BoxWithConstraints(modifier = modifier) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(x = (slideOffset.value * widthPx).roundToInt(), y = 0) },
        ) {
            pages[displayedPage]()
        }
    }
}
