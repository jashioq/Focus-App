package presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import focus.composeapp.generated.resources.Res
import focus.composeapp.generated.resources.sample_home_screen_message
import org.jetbrains.compose.resources.stringResource
import presentation.compose.component.text.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .displayCutoutPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        Text(
            text = stringResource(Res.string.sample_home_screen_message),
        )
    }
}
