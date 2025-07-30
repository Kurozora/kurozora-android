package app.kurozora.tracker.ui.components.show

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.kurozora.tracker.ui.home.ShowLoadingTaskState
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter

@Composable
fun FeaturedShowRow(uiState: ShowLoadingTaskState) {
    when (uiState) {
        is ShowLoadingTaskState.Loaded -> {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(1f),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                for (show in uiState.result)
                    FeaturedShowItem(modifier = Modifier.fillMaxWidth(0.8f),show = show)
            }
        }
        is ShowLoadingTaskState.Loading -> {
            Row(Modifier.fillMaxWidth().fillMaxHeight() , verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator()
            }
        }
        else -> {
            Text(text = "loading")
        }
    }

}