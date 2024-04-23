package app.kurozora.tracker.ui.components.show

import ResponsiveText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kurozora.tracker.api.response.show.Show
import app.kurozora.tracker.ui.theme.Global
import coil.compose.SubcomposeAsyncImage

@Composable
fun FeaturedShowItem(modifier: Modifier, show : Show) {
    Column(
        modifier = Modifier
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(bottom = 20.dp),
            color = Global.shared.separatorColor
        )

        Box(
            modifier = modifier
                .height(300.dp)
                .width(350.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            val imageUrl = show.attributes.banner?.url ?: show.attributes.poster?.url

            SubcomposeAsyncImage(
                modifier = Modifier
                    .matchParentSize(),
                model = imageUrl,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = 25f
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                TextRow(
                    title = show.attributes.title,
                    subTitle = show.attributes.tagline ?: ""
                )
            }
        }
    }
}

@Composable
private fun TextRow(title : String , subTitle : String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.50F),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    start = 8.dp,
                    bottom = 8.dp
                ),
            verticalArrangement = Arrangement.Bottom
        ) {
            ResponsiveText(
                text = title,
                textAlign = TextAlign.Start,
                targetTextSizeHeight = 16.sp,
                maxLines = 2,
                color = Color.White
            )

            ResponsiveText(
                text = subTitle,
                textAlign = TextAlign.Start,
                targetTextSizeHeight = 10.sp,
                maxLines = 1,
                color = Global.shared.subTextColor
            )
        }

        Row(
            modifier = Modifier
                .padding(end = 8.dp)
                .background(Global.shared.tintColor, RoundedCornerShape(10.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = "Add",
                color = Global.shared.tintedButtonTextColor
            )

            Icon(
                modifier = Modifier.padding(2.dp),
                imageVector = Icons.Outlined.ArrowDropDown,
                tint = Global.shared.tintedButtonTextColor,
                contentDescription = "Add"
            )
        }
    }
}
