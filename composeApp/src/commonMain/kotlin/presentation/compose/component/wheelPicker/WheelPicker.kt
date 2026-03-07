package presentation.compose.component.wheelPicker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WheelPicker(
    sections: List<WheelPickerSection>,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 36.dp,
    visibleItemCount: Int = 7,
    flingVelocityMultiplier: Float = 0.4f,
    onSelectionChanged: (sectionIndex: Int, itemIndex: Int) -> Unit = { _, _ -> },
    onItemSelected: ((sectionIndex: Int, item: String) -> Unit)? = null,
    onItemHighlighted: ((sectionIndex: Int, item: String) -> Unit)? = null,
) {
    val pickerHeight = itemHeight * visibleItemCount

    Box(modifier = modifier.height(pickerHeight)) {
        Row(modifier = Modifier.fillMaxSize()) {
            sections.forEachIndexed { sectionIndex, section ->
                Box(
                    modifier = Modifier.weight(1f).height(pickerHeight).clipToBounds(),
                ) {
                    WheelColumnPicker(
                        items = section.items,
                        initialIndex = section.initialIndex,
                        itemHeight = itemHeight,
                        visibleItemCount = visibleItemCount,
                        enabled = section.enabled,
                        flingVelocityMultiplier = flingVelocityMultiplier,
                        onIndexChanged = { index -> onSelectionChanged(sectionIndex, index) },
                        onItemSelected = onItemSelected?.let { cb ->
                            { item -> cb(sectionIndex, item) }
                        },
                        onItemHighlighted = onItemHighlighted?.let { cb ->
                            { item -> cb(sectionIndex, item) }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                    section.leadingContent?.let { leading ->
                        Box(
                            modifier = Modifier.align(Alignment.CenterStart),
                            contentAlignment = Alignment.Center,
                        ) { leading() }
                    }
                    section.trailingContent?.let { trailing ->
                        Box(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            contentAlignment = Alignment.Center,
                        ) { trailing() }
                    }
                }
            }
        }
    }
}
