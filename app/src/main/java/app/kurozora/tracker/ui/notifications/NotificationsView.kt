package app.kurozora.tracker.ui.notifications

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.kurozora.tracker.ui.theme.Global
import app.kurozora.tracker.ui.theme.KurozoraTheme

@Composable
fun NotificationsView() {
    val viewModel = NotificationsViewModel()

    KurozoraTheme {
        Surface(color = Global.shared.background, modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 5.dp)
                    .scrollable(rememberScrollState(), Orientation.Horizontal),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            }

            Text(text = "Notifications")
        }
    }
}