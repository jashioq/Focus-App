import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import domain.model.OnboardingState
import navigation.MainNavHost
import navigation.OnboardingNavHost
import navigation.viewModel.NavigationViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import presentation.util.koinViewModel

@Composable
@Preview
fun App() {
    Theme {
        KoinContext {
            Box(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.systemBars.union(WindowInsets.displayCutout)
                ),
            ) {
                GetNavHost()
            }
        }
    }
}

/**
 * Uses [NavigationViewModel] to determine which [NavHost] should be displayed.
 * @param navigationViewModel the [NavigationViewModel] to be used.
 */
@Composable
private fun GetNavHost(
    navigationViewModel: NavigationViewModel = koinViewModel(),
) {
    val onboardingState by navigationViewModel.state.collectAsState()

    when (onboardingState) {
        OnboardingState.LOADING -> {
            // Do nothing
        }
        OnboardingState.FINISHED -> MainNavHost()
        OnboardingState.NOT_FINISHED -> OnboardingNavHost()
    }
}
