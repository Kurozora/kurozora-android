package app.kurozora.tracker.ui.components.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.kurozora.tracker.ui.home.ExploreUiState
import app.kurozora.tracker.ui.home.GamesLoadingTaskState
import coil.compose.SubcomposeAsyncImage

@Composable
fun GamesRow(uiState: GamesLoadingTaskState){
    when(uiState){
        is  GamesLoadingTaskState.Loaded ->{
            uiState.result.forEach {
                Box(modifier = Modifier
                    .height(300.dp)
                    .width(350.dp)
                    .clip(RoundedCornerShape(4.dp))) {
                    val imageUrl = it.poster.url

                    SubcomposeAsyncImage(
                        modifier = Modifier.matchParentSize(),
                        model = imageUrl,
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.fillMaxSize() , verticalArrangement = Arrangement.Bottom) {
                        Text( text = it.title)
                       // TextRow(title = it.title ?: "" , subTitle = show.attributes.tagLine ?: "")
                    }
                }
            }
        }
        else ->{

        }
    }

}
