package app.kurozora.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozora.composeapp.generated.resources.Res
import kurozorakit.data.models.person.Person
import org.jetbrains.compose.resources.decodeToImageBitmap

@Composable
fun PersonCard(
    person: Person,
    subTitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var personPlaceholder: ByteArray? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        personPlaceholder = Res.readBytes("files/static/placeholders/person_poster.webp")
    }
    Column(
        modifier = modifier
            .width(120.dp)
            .wrapContentHeight()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KamelImage(
                resource = { asyncPainterResource(person.attributes.profile?.url.toString()) },
                contentDescription = person.attributes.fullName,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                onFailure = {
                    personPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Placeholder avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                onLoading = {
                    personPlaceholder?.decodeToImageBitmap()?.let { bitmap ->
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Loading avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = person.attributes.fullName,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subTitle ?: "",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
