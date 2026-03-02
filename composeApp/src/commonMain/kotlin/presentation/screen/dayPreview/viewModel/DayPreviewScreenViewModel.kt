package presentation.screen.dayPreview.viewModel

import kotlinx.coroutines.CoroutineScope
import presentation.screen.dayPreview.DayPreviewScreenAction
import presentation.screen.dayPreview.DayPreviewScreenState
import presentation.util.CoreViewModel
import util.Logger

class DayPreviewScreenViewModel(
    date: String,
    scope: CoroutineScope? = null,
    logger: Logger? = null,
) : CoreViewModel<DayPreviewScreenState, DayPreviewScreenAction>(
    initialState = DayPreviewScreenState(date = date),
    scope = scope,
    logger = logger,
) {
    override fun DayPreviewScreenAction.process() {}
}
