package app.kurozora.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kurozorakit.data.models.media.MediaStat

@Composable
fun RatingsAndReviewsCard(mediaStat: MediaStat) {
    val textColor = Color.White
    val accentColor = Color(0xFFFF9800)
    val barColor = Color(0xFF5B6080)

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "%.1f".format(mediaStat.ratingAverage),
                    color = textColor,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "out of 5",
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "${mediaStat.highestRatingPercentage.toInt()}%",
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mediaStat.sentiment,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
        // Rating bars
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val total = mediaStat.ratingCountList.sum().toFloat()
            val ratios = mediaStat.ratingCountList.map { it / total }.reversed()

            for (i in 5 downTo 1) {
                val ratio = ratios[5 - i]

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.width(80.dp),  // sabit genişlik → hizalanma garantisi
                        horizontalArrangement = Arrangement.End // sağa yasla
                    ) {
                        repeat(i) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .height(6.dp)
                            .weight(1f),
                        color = accentColor,
                        trackColor = barColor,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                }
            }
        }

        Text(
            text = "%,d Ratings".format(mediaStat.ratingCount),
            color = textColor.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
