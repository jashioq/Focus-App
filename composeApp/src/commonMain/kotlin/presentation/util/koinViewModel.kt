package presentation.util

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.currentKoinScope

/**
 * Used to locate and retrieve a [T] ViewModel instance from Koin scope.
 */
@Composable
inline fun <reified T : ViewModel> koinViewModel(): T {
    val scope = currentKoinScope()
    return viewModel {
        scope.get<T>()
    }
}
