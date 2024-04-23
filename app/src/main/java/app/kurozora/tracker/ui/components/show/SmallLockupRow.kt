package app.kurozora.tracker.ui.components.show

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.kurozora.tracker.ui.home.ShowLoadingTaskState

@Composable
fun SmallLockupRow(uiState: ShowLoadingTaskState) {
    when (uiState) {
        is ShowLoadingTaskState.Loaded -> {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(1f),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                for (show in uiState.result) {
                    SmallLockupView(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        imageURL = show.attributes.poster?.url ?: "",
                        title = show.attributes.title,
                        subtitle = show.attributes.tagline ?: ""
                    )
                }
            }
        }
        else -> {}
    }
}
