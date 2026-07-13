package app.kurozora.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import app.kurozora.ui.theme.ThemeConfig
import app.kurozora.ui.theme.ThemeType
import app.kurozora.ui.theme.colorSchemeFromConfig
import app.kurozora.ui.theme.CustomTheme

sealed class ThemeCardData {
    data class BuiltIn(val type: ThemeType, val title: String, val subtitle: String) : ThemeCardData()
    data class StoreItem(
        val appTheme: kurozorakit.data.models.theme.app.AppTheme,
        val screenshotUrls: List<String>,
    ) : ThemeCardData()
    data class Downloaded(val customTheme: CustomTheme) : ThemeCardData()
}

@Composable
fun AppThemeCard(
    data: ThemeCardData,
    isActive: Boolean,
    onAction: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(16.dp)
    val colors = when (data) {
        is ThemeCardData.BuiltIn -> {
            val scheme = colorSchemeFromConfig(ThemeConfig.BuiltIn(data.type))
            Triple(scheme.background, scheme.surfaceVariant, scheme.onBackground)
        }
        is ThemeCardData.StoreItem -> {
            Triple(Color(0xFF1A1A2E), Color(0xFF16213E), Color.White)
        }
        is ThemeCardData.Downloaded -> {
            val scheme = colorSchemeFromConfig(ThemeConfig.Custom(data.customTheme))
            Triple(scheme.background, scheme.surfaceVariant, scheme.onBackground)
        }
    }
    val backgroundColor1 = colors.first
    val backgroundColor2 = colors.second
    val textColor = colors.third
    val actionLabel = if (isActive) "USING" else "GET"

    Card(
        shape = cardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(backgroundColor1, backgroundColor2)
                        )
                    )
                    .padding(16.dp),
            ) {
                HeaderRow(
                    data = data,
                    textColor = textColor,
                    actionLabel = actionLabel,
                    isActive = isActive,
                    onAction = onAction,
                )

                Spacer(Modifier.height(16.dp))

                when (data) {
                    is ThemeCardData.BuiltIn -> ThemeColorSwatches(data.type)
                    is ThemeCardData.StoreItem -> ThemeScreenshotsRow(data.screenshotUrls)
                    is ThemeCardData.Downloaded -> ThemeColorSwatches(data.customTheme)
                }
            }

            if (onDelete != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete theme",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderRow(
    data: ThemeCardData,
    textColor: Color,
    actionLabel: String,
    isActive: Boolean,
    onAction: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val title = when (data) {
                is ThemeCardData.BuiltIn -> data.title
                is ThemeCardData.StoreItem -> data.appTheme.attributes.name
                is ThemeCardData.Downloaded -> data.customTheme.name
            }
            val subtitle = when (data) {
                is ThemeCardData.BuiltIn -> data.subtitle
                is ThemeCardData.StoreItem -> "v${data.appTheme.attributes.version} · ${data.appTheme.attributes.downloadCount} downloads"
                is ThemeCardData.Downloaded -> "v${data.customTheme.version}"
            }

            Text(
                text = title,
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                color = textColor.copy(alpha = 0.75f),
                fontSize = 14.sp,
            )
        }

        Spacer(Modifier.width(12.dp))

        Button(
            onClick = onAction,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White,
            ),
            modifier = Modifier.height(40.dp),
        ) {
            Text(
                text = actionLabel,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun ThemeColorSwatches(type: ThemeType) {
    val scheme = colorSchemeFromConfig(ThemeConfig.BuiltIn(type))
    ColorSwatches(
        scheme.primary,
        scheme.background,
        scheme.surfaceVariant,
        scheme.tertiaryContainer,
        scheme.onSurface,
        scheme.onSurfaceVariant,
    )
}

@Composable
private fun ThemeColorSwatches(theme: CustomTheme) {
    val scheme = colorSchemeFromConfig(ThemeConfig.Custom(theme))
    ColorSwatches(
        scheme.primary,
        scheme.background,
        scheme.surfaceVariant,
        scheme.tertiaryContainer,
        scheme.onSurface,
        scheme.onSurfaceVariant,
    )
}

@Composable
private fun ColorSwatches(
    primary: Color,
    background: Color,
    surfaceVariant: Color,
    tintedBackground: Color,
    textColor: Color,
    subTextColor: Color,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        listOf(
            primary to "Primary",
            background to "Background",
            surfaceVariant to "Surface",
            tintedBackground to "Tinted",
            textColor to "Text",
            subTextColor to "SubText",
        ).forEach { (color, _) ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
            )
        }
    }
}

@Composable
private fun ThemeScreenshotsRow(screenshotUrls: List<String>) {
    val urls = if (screenshotUrls.size >= 3) screenshotUrls.take(3) else screenshotUrls

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(urls) { url ->
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                KamelImage(
                    resource = { asyncPainterResource(url) },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        if (urls.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .aspectRatio(9f / 16f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("?", color = Color.White.copy(alpha = 0.5f))
                }
            }
        }
    }
}
