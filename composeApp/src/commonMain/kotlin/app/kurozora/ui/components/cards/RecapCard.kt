package app.kurozora.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozora.composeapp.generated.resources.Res
import kurozorakit.data.models.recap.Recap
import org.jetbrains.compose.resources.painterResource

@Composable
fun RecapCard(
    recap: Recap,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Yılı alıp son iki hanesini '25 formatına getiriyoruz
    val yearSuffix = recap.attributes.year.toString().takeLast(2)
    var logo: ByteArray? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        logo = Res.readBytes("files/static/icon/logo.webp")
    }

    Box(
        modifier = modifier
            .width(300.dp)
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        parseColor(recap.attributes.backgroundColor2),
                        parseColor(recap.attributes.backgroundColor1)
                    )
                )
            )
    ) {
        // Arka Plan Görseli (Artwork)
        KamelImage(
            resource = { asyncPainterResource(recap.attributes.artwork?.url.toString()) },
            contentDescription = recap.attributes.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.8f)
        )

        Text(
            text = "'$yearSuffix",
            fontSize = 200.sp,
            fontWeight = FontWeight.Black,
            color = Color.White.copy(alpha = 0.75f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
//                .offset(y = 20.dp)
        )

        // Sol Üst: Re:Cap Yazısı
        Text(
            text = "Re:Cap",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        logo?.decodeToImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = "Kurozora Logo",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(24.dp)
            )
        }

        Text(
            text = recap.attributes.month.toString().uppercase(),
            color = Color.White.copy(0.7f),
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}