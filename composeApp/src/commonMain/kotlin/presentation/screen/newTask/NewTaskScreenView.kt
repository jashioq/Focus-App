package presentation.screen.newTask

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.compose.PlatformBackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import focus.composeapp.generated.resources.Res
import focus.composeapp.generated.resources.circle_arrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import presentation.compose.component.border.tiltBorder

private val BoxShape = RoundedCornerShape(32.dp)

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

    val isEnterComplete = !animatedVisibilityScope.transition.isRunning &&
        animatedVisibilityScope.transition.currentState == EnterExitState.Visible

    LaunchedEffect(isEnterComplete) {
        if (isEnterComplete) {
            contentAlpha.animateTo(1f, animationSpec = tween(300))
        }
    }

    val handleBack = {
        scope.launch {
            contentAlpha.animateTo(0f, animationSpec = tween(200))
            onBack()
        }
    }

    PlatformBackHandler { handleBack() }

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

            Text(
                text = date,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = "Bottom content",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
