package com.seloreis.kurozora.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozoraapp.composeapp.generated.resources.Res
import kurozorakit.data.models.song.Song
import org.jetbrains.compose.resources.decodeToImageBitmap

@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit
) {
    var songPlaceholder: ByteArray? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        songPlaceholder = Res.readBytes("files/static/placeholders/music_album.webp")
    }
    Column(
        modifier = Modifier
            .width(280.dp)
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
                // Şarkı görseli
                KamelImage({ asyncPainterResource(song.attributes.artwork?.url.orEmpty()) },
                    contentDescription = "Song artwork",
                    modifier = Modifier.
                        fillMaxSize()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    alpha = DefaultAlpha,
                    onFailure = {
                        songPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Placeholder avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    },
                    onLoading = {
                        songPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Loading avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                )

                // Hafif degrade overlay efekti
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(
//                            brush = Brush.verticalGradient(
//                                colors = listOf(
//                                    Color.Transparent,
//                                    Color(0x66000000)
//                                )
//                            )
//                        )
//                )

                // Sağ üstte "Music" etiketi
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                        .padding(8.dp)
//                        .background(
//                            color = Color(0x33FFFFFF),
//                            shape = RoundedCornerShape(8.dp)
//                        )
//                        .padding(horizontal = 6.dp, vertical = 2.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "Music",
//                        color = Color.White,
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                }
            }

            Spacer(modifier = Modifier.height(30.dp))

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
