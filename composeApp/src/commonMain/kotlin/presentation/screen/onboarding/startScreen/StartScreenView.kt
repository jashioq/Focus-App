package presentation.screen.onboarding.startScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import focus.composeapp.generated.resources.Res
import focus.composeapp.generated.resources.sample_start_button
import org.jetbrains.compose.resources.stringResource
import presentation.compose.component.button.PrimaryButton

@Composable
fun StartScreenView(
    modifier: Modifier = Modifier,
    onPrimaryButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        PrimaryButton(
            text = stringResource(Res.string.sample_start_button),
            onClick = onPrimaryButtonClick,
        )
    }
}
