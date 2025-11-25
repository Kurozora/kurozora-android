package app.kurozora.ui.screens.profile.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.core.settings.AccountManager
import app.kurozora.core.settings.AccountScopedSettings
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kurozora.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    windowWidth: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
) {
    val accountManager: AccountManager = koinInject()
    val scopedSettings = accountManager.getScopedSettings()!!
    val categories = remember { generateSettingsCategories(scopedSettings) }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }
    val isLargeScreen = windowWidth == WindowWidthSizeClass.EXPANDED

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLargeScreen) {
            Row(Modifier.fillMaxSize().padding(padding)) {
                Column(
                    Modifier
                        .width(400.dp)
                        .fillMaxHeight()
                ) {
                    AccountSwitcher(accountManager)

                    categories.forEach { category ->
                        val isSelected = selectedCategory == category

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { selectedCategory = category }
                                .background(
                                    if (isSelected)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else
                                        Color.Transparent
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            category.icon?.invoke()

                            Column(Modifier.padding(start = if (category.icon != null) 8.dp else 0.dp)) {
                                Text(category.title)
                                category.subtitle?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }


                Box(Modifier.weight(1f).fillMaxHeight()) {
                    selectedCategory?.let { SettingsCategoryDetail(it, scopedSettings) }
                }
            }
        } else {
            var showDialog by remember { mutableStateOf(false) }
            var activeCategory by remember { mutableStateOf<SettingsCategory?>(null) }

            LazyColumn(Modifier.padding(padding)) {
                item {
                    AccountSwitcher(accountManager, onAddAccount = onNavigateToLoginScreen)
                }
                items(categories) { category ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                activeCategory = category
                                showDialog = true
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        category.icon?.invoke()
                        Column(Modifier.padding(start = if (category.icon != null) 8.dp else 0.dp)) {
                            Text(category.title)
                            category.subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }

            activeCategory?.let { category ->
                if (showDialog) {
                    Dialog(
                        onDismissRequest = { showDialog = false }, properties = DialogProperties(
                            usePlatformDefaultWidth = false
                        )
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Column(Modifier.fillMaxSize()) {
                                TopAppBar(
                                    title = { Text(category.title) },
                                    navigationIcon = {
                                        IconButton(onClick = { showDialog = false }) {
                                            Icon(Icons.Default.Close, contentDescription = "Close")
                                        }
                                    }
                                )
                                SettingsCategoryDetail(category, scopedSettings)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCategoryDetail(category: SettingsCategory, scopedSettings: AccountScopedSettings) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(category.items) { item ->
            SettingsItemRow(item, scopedSettings)
//            when (item) {
////                is SettingItem.SwitchSetting -> SwitchSettingItem(item, scopedSettings)
////                is SettingItem.SingleSelectSetting -> SingleSelectSettingItem(item, scopedSettings)
////                is SettingItem.MultiSelectSetting -> MultiSelectSettingItem(item, scopedSettings)
////                is SettingItem.CustomSetting -> item.content.invoke()
//            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItemRow(
    item: SettingItem,
    scopedSettings: AccountScopedSettings,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                when (item) {
                    is SettingItem.CustomSetting -> {
                        if (item.isFullDialog) {
                            showDialog = true     // sadece custom + fullscreen
                        }
                    }
                    else -> {
                        // Other setting types behave normally (Switch, SingleSelect, etc.)
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Inline UI only when NOT fullscreen
        when (item) {
            is SettingItem.CustomSetting -> {
                if (!item.isFullDialog) {
                    item.content.invoke()
                } else {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.title)
                        item.subtitle?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            is SettingItem.SwitchSetting -> SwitchSettingItem(item, scopedSettings)
            is SettingItem.SingleSelectSetting -> SingleSelectSettingItem(item, scopedSettings)
            is SettingItem.MultiSelectSetting -> MultiSelectSettingItem(item, scopedSettings)
        }
    }

    // FULL SCREEN DIALOG — only for CustomSetting + isFullDialog = true
    if (item is SettingItem.CustomSetting && item.isFullDialog && showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }, properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = { Text(item.title) },
                        navigationIcon = {
                            IconButton(onClick = { showDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                    )
                    item.content.invoke()
                }
            }
        }
    }
}

@Composable
fun SwitchSettingItem(item: SettingItem.SwitchSetting, scopedSettings: AccountScopedSettings) {
    var checked by remember { mutableStateOf(scopedSettings[item.key]?.toBoolean() ?: item.value) }
    // Subtitle dynamically show "On" / "Off"
    val subtitle = if (checked) "On" else "Off"

    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { checked = !checked },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(item.title)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = {
            checked = it
            scopedSettings[item.key] = checked.toString()
        })
    }
}

@Suppress("UnrememberedMutableState")
@Composable
fun SingleSelectSettingItem(item: SettingItem.SingleSelectSetting, scopedSettings: AccountScopedSettings) {
    var showDialog by remember { mutableStateOf(false) }
    // Seçili değer her zaman scopedSettings üzerinden okunuyor
    val selected by derivedStateOf { scopedSettings[item.key] ?: item.options.first() }

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(item.title)
                Text(selected, style = MaterialTheme.typography.bodySmall)
            }
        }

        if (showDialog) {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = { showDialog = false },
                confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Close") } },
                title = { Text(item.title) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.options.forEach { option ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scopedSettings[item.key] = option
                                        showDialog = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selected == option, onClick = {
                                    scopedSettings[item.key] = option
                                    showDialog = false
                                })
                                Spacer(Modifier.width(8.dp))
                                Text(option)
                            }
                        }
                    }
                }
            )
        }
    }
}

@Suppress("UnrememberedMutableState")
@Composable
fun MultiSelectSettingItem(item: SettingItem.MultiSelectSetting, scopedSettings: AccountScopedSettings) {
    var showDialog by remember { mutableStateOf(false) }
    // Seçili değer her zaman scopedSettings üzerinden okunuyor
    val selected = remember { mutableStateListOf<String>() }

    LaunchedEffect(scopedSettings[item.key]) {
        val list = scopedSettings[item.key]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        selected.clear()
        selected.addAll(list)
    }
    val subtitle by derivedStateOf { if (selected.isEmpty()) "None" else selected.joinToString(", ") }

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(item.title)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }

        if (showDialog) {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = { showDialog = false },
                confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Done") } },
                title = { Text(item.title) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.options.forEach { option ->
                            val isChecked = selected.contains(option)
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selected.remove(option) else selected.add(option)
                                        scopedSettings[item.key] = selected.joinToString(",")
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) selected.add(option) else selected.remove(option)
                                        scopedSettings[item.key] = selected.joinToString(",")
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(option)
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun AccountSwitcher(
    accountManager: AccountManager,
    modifier: Modifier = Modifier,
    onAddAccount: () -> Unit = {}, // login/signup sayfasına yönlendirme
) {
    val activeAccount by accountManager.activeAccount.collectAsState()
    val accounts = accountManager.getAllAccounts()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var profilePlaceholder: ByteArray? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        profilePlaceholder = Res.readBytes("files/static/placeholders/user_profile.webp")
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LOGOUT BUTTON + ROTATION
        IconButton(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.width(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ----- HESAPLAR -----
            items(accounts.size) { index ->
                val account = accounts[index]
                val isActive = account.id == activeAccount?.id

                Column(
                    modifier = Modifier
                        .width(100.dp)
                        .clickable { accountManager.switchAccount(account.id) }
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    KamelImage(
                        { asyncPainterResource(account.profileUrl.toString()) },
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        onFailure = {
                            profilePlaceholder?.decodeToImageBitmap()?.let { bmp ->
                                Image(
                                    bitmap = bmp,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        },
                        onLoading = {
                            profilePlaceholder?.decodeToImageBitmap()?.let { bmp ->
                                Image(
                                    bitmap = bmp,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = account.username,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
            }
            // ----- HER ZAMAN GÖRÜNEN ADD PROFILE BUTTON -----
            item {
                Column(
                    modifier = Modifier
                        .width(100.dp)
                        .clickable { onAddAccount() }
                        .background(Color.Transparent)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add account",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "Add",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
    // ----- LOGOUT CONFIRMATION DIALOG -----
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    accountManager.logout()
                }) { Text("Logout") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Logout") },
            text = { Text("Do you really want to logout?") }
        )
    }
}




