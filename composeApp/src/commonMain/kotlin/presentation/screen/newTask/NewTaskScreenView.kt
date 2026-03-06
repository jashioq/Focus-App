package presentation.screen.newTask

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import focus.composeapp.generated.resources.Res
import focus.composeapp.generated.resources.circle_arrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import presentation.compose.PlatformBackHandler
import presentation.compose.component.border.tiltBorder
import presentation.compose.component.button.PrimaryButton
import kotlin.math.roundToInt

private val BoxShape = RoundedCornerShape(32.dp)
private const val PAGE_COUNT = 4

private val pageNames = listOf(
    "Name & Description",
    "Schedule",
    "Sessions",
    "Confirmation",
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NewTaskScreenView(
    date: String,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val scope = rememberCoroutineScope()
    val contentAlpha = remember { Animatable(0f) }

    var currentPage by remember { mutableIntStateOf(0) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    // Offset animatable for carousel slide: 0f = current page centered
    val slideOffset = remember { Animatable(0f) }

    val isEnterComplete = !animatedVisibilityScope.transition.isRunning &&
        animatedVisibilityScope.transition.currentState == EnterExitState.Visible

    LaunchedEffect(isEnterComplete) {
        if (isEnterComplete) {
            contentAlpha.animateTo(1f, animationSpec = tween(300))
        }
    }

    val navigateTo: (Int) -> Unit = { targetPage ->
        scope.launch {
            val direction = if (targetPage > currentPage) 1f else -1f
            // slide out current
            slideOffset.animateTo(
                direction * -1f,
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            )
            currentPage = targetPage
            // snap to incoming side
            slideOffset.snapTo(direction * 1f)
            // slide in new page
            slideOffset.animateTo(
                0f,
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            )
        }
    }

    val handleBack = {
        if (currentPage > 0) {
            navigateTo(currentPage - 1)
        } else {
            showDiscardDialog = true
        }
    }

    val doClose = {
        scope.launch {
            contentAlpha.animateTo(0f, animationSpec = tween(200))
            onBack()
        }
    }

    PlatformBackHandler { handleBack() }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Are you sure?") },
            text = { Text("Discard this new task?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    doClose()
                }) {
                    Text("Discard changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Keep editing")
                }
            },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "add-task-glass"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        clipInOverlayDuringTransition = OverlayClip(BoxShape),
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                        enter = EnterTransition.None,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 400, easing = FastOutSlowInEasing)
                        },
                    )
                    .tiltBorder(
                        color = primaryColor,
                        thickness = 1.dp,
                        upperAlpha = 0.5f,
                        shape = BoxShape,
                    )
                    .clip(BoxShape)
                    .background(Color.White.copy(alpha = 0.09f)),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha.value },
        ) {
            // Back button
            IconButton(
                onClick = { handleBack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 8.dp, start = 8.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.circle_arrow),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.graphicsLayer { scaleX = -1f },
                )
            }

            // Carousel pages
            CarouselPage(
                slideOffset = slideOffset.value,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp, bottom = 120.dp)
                    .padding(horizontal = 24.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = pageNames[currentPage],
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            // Bottom area: dots + optional done button
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PageDots(currentPage = currentPage, pageCount = PAGE_COUNT)

                Spacer(Modifier.height(24.dp))
                if (currentPage == PAGE_COUNT - 1) {
                    PrimaryButton(
                        text = "Done",
                        onClick = { doClose() },
                    )
                } else {
                    PrimaryButton(
                        text = "Next",
                        onClick = { navigateTo(currentPage + 1) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CarouselPage(
    slideOffset: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // slideOffset is in [-1, 1] where 0 = fully visible
    // We use the full width for offset distance
    val density = LocalDensity.current
    Box(modifier = modifier) {
        // We need the actual width at layout time — use BoxWithConstraints-like approach via onGloballyPositioned,
        // but simpler: just use a fraction of a large assumed width. We'll use offset fraction in dp
        // instead by computing inside the Box content via layout size.
        OffsetContent(slideOffset = slideOffset, content = content)
    }
}

@Composable
private fun OffsetContent(
    slideOffset: Float,
    content: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(x = (slideOffset * widthPx).roundToInt(), y = 0) },
        ) {
            content()
        }
    }
}

@Composable
private fun PageDots(currentPage: Int, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val filled = index == currentPage
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (filled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                    ),
            )
        }
    }
}
