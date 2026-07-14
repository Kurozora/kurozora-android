package app.kurozora.ui.screens.profile.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.kurozora.core.icons.AppIconData
import app.kurozora.core.icons.AppIconManager
import app.kurozora.core.icons.getAllAppIconCategories
import app.kurozora.core.settings.AccountScopedSettings
import kurozora.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.koin.compose.koinInject

@Composable
fun AppIconPickerContent(
    scopedSettings: AccountScopedSettings,
    appIconManager: AppIconManager = koinInject(),
) {
    val categories = remember { getAllAppIconCategories() }
    val managerIcon = remember { appIconManager.getCurrentIcon() }
    var selectedIcon by remember { mutableStateOf(managerIcon ?: scopedSettings.icon) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        categories.forEach { category ->
            item(key = "header_${category.key}") {
                CategoryHeader(category.displayName)
            }

            category.icons.chunked(3).forEachIndexed { rowIndex, rowIcons ->
                item(key = "row_${category.key}_$rowIndex") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowIcons.forEach { icon ->
                            IconItem(
                                icon = icon,
                                isSelected = selectedIcon == icon.identifier,
                                onClick = {
                                    selectedIcon = icon.identifier
                                    scopedSettings.icon = icon.identifier
                                    appIconManager.setIcon(icon.identifier)
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rowIcons.size < 3) {
                            repeat(3 - rowIcons.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun IconItem(
    icon: AppIconData,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var bitmap by remember(icon.resourcePath) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(icon.resourcePath) {
        try {
            val bytes = Res.readBytes(icon.resourcePath)
            bitmap = bytes.decodeToImageBitmap()
        } catch (_: Exception) {
        }
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val borderWidth = if (isSelected) 3.dp else 1.dp

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!,
                    contentDescription = icon.displayName,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentScale = ContentScale.Fit,
                )
            } else {
                Text(
                    text = icon.displayName.take(2),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(4.dp),
                        ),
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = icon.displayName,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
