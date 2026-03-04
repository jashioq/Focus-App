package presentation.screen.newTask

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import presentation.compose.component.border.tiltBorder

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NewTaskScreenView(
    date: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val cornerRadius by animatedVisibilityScope.transition.animateFloat(
        label = "cornerRadius",
    ) { state ->
        when (state) {
            EnterExitState.PreEnter, EnterExitState.PostExit -> 1000f
            EnterExitState.Visible -> 32f
        }
    }
    val shape = RoundedCornerShape(cornerRadius.dp)

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
                        clipInOverlayDuringTransition = OverlayClip(shape),
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                        enter = EnterTransition.None,
                    )
                    .tiltBorder(
                        color = primaryColor,
                        thickness = 1.dp,
                        upperAlpha = 0.5f,
                        shape = shape,
                    )
                    .clip(shape)
                    .background(Color.White.copy(alpha = 0.09f)),
            ) {
                Text(
                    text = date,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}
