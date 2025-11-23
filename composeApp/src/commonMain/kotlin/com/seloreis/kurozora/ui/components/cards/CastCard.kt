package com.seloreis.kurozora.ui.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozorakit.data.models.show.cast.Cast

@Composable
fun CastCard(
    cast: Cast,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val person = cast.relationships.people.data.first()
    val character = cast.relationships.characters.data.first()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(min = 400.dp)
            .height(180.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Soldaki Person resmi
            KamelImage(
                resource = { asyncPainterResource(person.attributes.profile?.url ?: "") },
                contentDescription = "${person.attributes.fullName} image",
                modifier = Modifier
                    .width(120.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
                alpha = DefaultAlpha
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Ortadaki Column — yüksekliği içeriğe göre otomatik, kırpılmaz
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(), //
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Üst: person info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = person.attributes.fullName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        text = cast.attributes.role.name,
//                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
//                        modifier = Modifier.alpha(0.6f),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Alt: character info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = character.attributes.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = cast.attributes.role.name,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        modifier = Modifier.alpha(0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Sağdaki Character resmi
            KamelImage(
                resource = { asyncPainterResource(character.attributes.profile?.url ?: "") },
                contentDescription = "${character.attributes.name} image",
                modifier = Modifier
                    .width(120.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
                alpha = DefaultAlpha
            )
        }
    }
}
