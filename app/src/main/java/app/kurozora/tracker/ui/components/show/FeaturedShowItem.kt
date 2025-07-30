package app.kurozora.tracker.ui.components.show

import ResponsiveText
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kurozora.tracker.R
import app.kurozora.tracker.api.response.show.Show
import app.kurozora.tracker.ui.theme.Gray50
import app.kurozora.tracker.ui.theme.Orange
import coil.compose.SubcomposeAsyncImage

@Composable
fun FeaturedShowItem(modifier: Modifier,show : Show){
            Box(modifier = modifier
                .height(300.dp)
                .width(350.dp)
                .clip(RoundedCornerShape(4.dp))) {
                val imageUrl = show.attributes.banner?.url ?: show.attributes.poster.url
                val scale =
                    if (show.attributes.banner?.url != null) ContentScale.Crop else ContentScale.FillBounds
                SubcomposeAsyncImage(
                    modifier = Modifier.matchParentSize(),
                    model = imageUrl,
                    contentDescription = "",
                    contentScale = scale
                )
               Column(modifier = Modifier.fillMaxSize() , verticalArrangement = Arrangement.Bottom) {
                    TextRow(title = show.attributes.title ?: "" , subTitle = show.attributes.tagLine ?: "")
                }
            }
        }

@Composable
private fun TextRow(title : String , subTitle : String){
    Row(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.25f) , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.SpaceBetween) {

        Column( modifier = Modifier
            .fillMaxWidth(0.7f)
            .fillMaxHeight()
            .padding(start = 5.dp) , verticalArrangement = Arrangement.Bottom){
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
                color = Gray50
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(end = 5.dp)
                .background(Orange, RoundedCornerShape(10.dp)), verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.Center
        ) {
            Text(modifier = Modifier.padding(start = 4.dp),text = "Add", color = Color.White)
            Icon(modifier = Modifier.padding(2.dp),imageVector = Icons.Outlined.ArrowDropDown, tint = Color.White , contentDescription = "Add")
        }
    }
}