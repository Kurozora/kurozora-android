package app.kurozora.tracker.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kurozora.tracker.model.AttributesSize
import app.kurozora.tracker.ui.components.game.GamesRow
import app.kurozora.tracker.ui.components.show.FeaturedShowRow
import app.kurozora.tracker.ui.theme.KurozoraTheme
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter

@Composable
fun ExploreView(){
    val viewModel = HomeViewModel()
    val data = viewModel.page
    val uiState by viewModel.uiState.collectAsState()

    KurozoraTheme {
    Surface(color = Color.DarkGray , modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 5.dp)
                    .scrollable(rememberScrollState(), Orientation.Horizontal),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
               items(items = data.value) { item ->
                   Column(modifier = Modifier
                       .fillMaxWidth()
                       .height(AttributesSize.getSize(item.attributes.size))
                       .padding(bottom = 10.dp) , horizontalAlignment = Alignment.Start) {
                           Text(text = item.attributes.title)
                       if (item.attributes.title == "Featured"){
                           FeaturedShowRow(uiState = uiState.featuredState)
                       }else{
                           GamesRow(uiState = uiState.gamesState)
                       }
                       Divider(
                           Modifier
                               .fillMaxWidth(0.9f)
                               .height(1.dp) , color = Color.White)
                       }

               }
            }
        }
    }
}