package presentation.screen.dayPreview

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.currentKoinScope
import org.koin.core.parameter.parametersOf
import presentation.screen.dayPreview.viewModel.DayPreviewScreenViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DayPreviewScreen(
    date: String,
    onNavigateToNewTask: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val scope = currentKoinScope()
    val viewModel: DayPreviewScreenViewModel = viewModel {
        scope.get<DayPreviewScreenViewModel>(parameters = { parametersOf(date) })
    }
    val state by viewModel.state.collectAsState()

    DayPreviewScreenView(
        state = state,
        onAddTaskClicked = { onNavigateToNewTask(state.date) },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
    )
}
