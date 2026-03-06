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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import focus.composeapp.generated.resources.Res
import focus.composeapp.generated.resources.circle_arrow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import presentation.compose.PlatformBackHandler
import presentation.compose.component.border.tiltBorder
import presentation.compose.component.button.PrimaryButton
import presentation.compose.component.pager.HorizontalCarousel
import presentation.compose.component.pager.PageIndicator
import presentation.screen.newTask.view.ConfirmationView
import presentation.screen.newTask.view.NameDescriptionView
import presentation.screen.newTask.view.ScheduleView
import presentation.screen.newTask.view.SessionsView

private val BoxShape = RoundedCornerShape(32.dp)
private const val PAGE_COUNT = 4

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

    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val defaultDeadline = remember(today) { today.plus(7, DateTimeUnit.DAY) }

    val isNextEnabled = currentPage != 0 || taskName.isNotBlank()

    val isEnterComplete = !animatedVisibilityScope.transition.isRunning &&
        animatedVisibilityScope.transition.currentState == EnterExitState.Visible

    LaunchedEffect(isEnterComplete) {
        if (isEnterComplete) {
            contentAlpha.animateTo(1f, animationSpec = tween(300))
        }
    }

    val handleBack = {
        if (currentPage > 0) currentPage-- else showDiscardDialog = true
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

            HorizontalCarousel(
                selectedPage = currentPage,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp, bottom = 120.dp)
                    .padding(horizontal = 24.dp),
            ) {
                page {
                    NameDescriptionView(
                        name = taskName,
                        description = taskDescription,
                        onNameChange = { taskName = it },
                        onDescriptionChange = { taskDescription = it },
                    )
                }
                page {
                    ScheduleView(
                        defaultStartDate = today,
                        defaultDeadline = defaultDeadline,
                    )
                }
                page { SessionsView() }
                page { ConfirmationView() }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PageIndicator(currentPage = currentPage, pageCount = PAGE_COUNT)

                Spacer(Modifier.height(24.dp))
                if (currentPage == PAGE_COUNT - 1) {
                    PrimaryButton(
                        text = "Done",
                        onClick = { doClose() },
                    )
                } else {
                    PrimaryButton(
                        text = "Next",
                        isEnabled = isNextEnabled,
                        onClick = { currentPage++ },
                    )
                }
            }
        }
    }
}
