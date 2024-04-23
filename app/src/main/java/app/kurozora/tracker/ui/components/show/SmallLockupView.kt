package app.kurozora.tracker.ui.components.show

import ResponsiveText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kurozora.tracker.ui.theme.Global
import coil.compose.SubcomposeAsyncImage

@Composable
fun SmallLockupView(
    modifier: Modifier,
    imageURL: String,
    title: String,
    subtitle: String,
) {
    Column(
        modifier = modifier
            .height(300.dp)
            .width(350.dp)
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier.aspectRatio(ratio = 9F / 16F)
                    .clip(RoundedCornerShape(18.dp)),
                model = imageURL,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                TextRow(
                    title = title,
                    subtitle = subtitle
                )
            }
        }
    }
}

@Composable
private fun TextRow(title: String , subtitle: String) {
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
                text = subtitle,
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
