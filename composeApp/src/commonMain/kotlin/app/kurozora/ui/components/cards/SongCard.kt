package app.kurozora.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozora.composeapp.generated.resources.Res
import kurozorakit.data.models.song.Song
import org.jetbrains.compose.resources.decodeToImageBitmap

@Composable
fun SongCard(
    song: Song,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var songPlaceholder: ByteArray? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        songPlaceholder = Res.readBytes("files/static/placeholders/music_album.webp")
    }
    Column(
        modifier = modifier
            .width(240.dp)
            .height(320.dp)
            .clickable { onClick() },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Üstte kapak görseli
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                KamelImage(
                    resource = { asyncPainterResource(song.attributes.artwork?.url.orEmpty()) },
                    contentDescription = "Song artwork",
                    modifier = Modifier.matchParentSize().clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop,
                    onFailure = {
                        songPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Placeholder avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillHeight
                            )
                        }
                    },
                    onLoading = {
                        songPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Loading avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            // Şarkı başlığı
            Text(
                text = song.attributes.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            )
            // Sanatçı adı
            Text(
                text = song.attributes.artist,
                color = Color(0xFFB0B4C3),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))
            // Bölüm bilgisi
//            Text(
//                text = "Episodes: ${song.attributes.}",
//                color = Color(0xFFB0B4C3),
//                fontSize = 12.sp,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.fillMaxWidth()
//            )
        }
    }
}
