package app.kurozora.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.kurozora.ui.components.cards.FeedMessageCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowWidthSizeClass
import kurozorakit.data.models.feed.message.FeedMessage
import kurozorakit.data.models.user.User
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    windowWidth: WindowWidthSizeClass,
    isLoggedIn: Boolean,
    currentUser: User?,
    onNavigateToItemDetail: (Any) -> Unit,
    viewModel: FeedViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val newMessageFocusRequester = remember { FocusRequester() }
    val isFabVisible by remember {
        derivedStateOf {
            !state.showMessageDetailDialog
        //&& listState.firstVisibleItemIndex == 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feed") },
                actions = {
                    IconButton(onClick = { viewModel.loadExploreFeed() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            if (isLoggedIn && isFabVisible) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.showNewMessageDialog()
                    },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "New Message") },
                    text = { Text("New Message") }
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val uiState = state.uiState) {
                is FeedUIState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is FeedUIState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Error loading feed",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = uiState.message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Button(onClick = { viewModel.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is FeedUIState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Feed Messages
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(uiState.feedMessages) { message ->
                                FeedMessageCard(
                                    feedMessage = message,
                                    isCurrentUser = currentUser?.id == message.relationships.users.data.firstOrNull()?.id,
                                    onClick = { viewModel.showMessageDetail(message) },
                                    onReshareClick = { viewModel.reshareMessage(it) },
                                    onHeartClick = { viewModel.heartMessage(it) },
                                    onReplyClick = { viewModel.showReplyDialog(it) },
                                    onEditClick = { viewModel.showEditDialog(it) },
                                    onDeleteClick = { viewModel.showDeleteDialog(it) },
                                    onReportClick = {},
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Load More Indicator
                            if (state.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            } else if (uiState.nextPageUrl != null) {
                                item {
                                    Button(
                                        onClick = { viewModel.loadMore() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Load More")
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Handle other states
                }
            }

            // New Message Dialog - Telefonlarda Fullscreen, tablet/bilgisayarda AlertDialog
            if (state.showNewMessageDialog) {
                // Cihaz genişliğine göre dialog tipini seç
                when (windowWidth) {
                    WindowWidthSizeClass.COMPACT,
                    WindowWidthSizeClass.MEDIUM -> {
                        // Telefon veya küçük tabletlerde Fullscreen Dialog
                        FullscreenNewMessageDialog(
                            content = state.newMessageContent,
                            isNSFW = state.isNSFW,
                            isSpoiler = state.isSpoiler,
                            postingMessage = state.postingMessage,
                            characterLimit = FeedMessage.maxCharacterLimit,
                            onContentChange = viewModel::updateNewMessageContent,
                            onNSFWToggle = viewModel::toggleNSFW,
                            onSpoilerToggle = viewModel::toggleSpoiler,
                            onSendClick = {
                                viewModel.postMessage()
                                viewModel.hideNewMessageDialog()
                            },
                            onDismiss = { viewModel.hideNewMessageDialog() },
                            focusRequester = newMessageFocusRequester
                        )
                    }

                    WindowWidthSizeClass.EXPANDED -> {
                        // Büyük tablet veya bilgisayarlarda AlertDialog
                        AlertDialog(
                            onDismissRequest = { viewModel.hideNewMessageDialog() },
                            title = { Text("New Message") },
                            text = {
                                NewMessageComposer(
                                    content = state.newMessageContent,
                                    isNSFW = state.isNSFW,
                                    isSpoiler = state.isSpoiler,
                                    postingMessage = state.postingMessage,
                                    characterLimit = kurozorakit.data.models.feed.message.FeedMessage.maxCharacterLimit,
                                    onContentChange = viewModel::updateNewMessageContent,
                                    onNSFWToggle = viewModel::toggleNSFW,
                                    onSpoilerToggle = viewModel::toggleSpoiler,
                                    onSendClick = {
                                        viewModel.postMessage()
                                        viewModel.hideNewMessageDialog()
                                    },
                                    focusRequester = newMessageFocusRequester,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.postMessage()
                                        viewModel.hideNewMessageDialog()
                                    },
                                    enabled = state.newMessageContent.trim().isNotEmpty() &&
                                            state.newMessageContent.length <= kurozorakit.data.models.feed.message.FeedMessage.maxCharacterLimit &&
                                            !state.postingMessage
                                ) {
                                    if (state.postingMessage) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = Color.White
                                        )
                                    } else {
                                        Text("Post")
                                    }
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { viewModel.hideNewMessageDialog() }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }

            if (state.showMessageDetailDialog && state.selectedMessageForDetail != null) {
                MessageDetailDialog(
                    message = state.selectedMessageForDetail!!,
                    currentUserId = currentUser?.id,
                    replies = state.messageReplies,
                    isLoadingReplies = state.isLoadingReplies,
                    repliesError = state.repliesError,
                    postingMessage = state.postingMessage,
                    windowWidth = windowWidth,
                    onDismiss = { viewModel.hideMessageDetail() },
                    onReplyClick = { viewModel.showReplyDialog(it) },
                    onHeartClick = { viewModel.heartMessage(it) },
                    onReshareClick = { viewModel.reshareMessage(it) },
                    onPostReply = { content ->
                        viewModel.postReplyToSelectedMessage(content)
                    },
                    viewModel = viewModel,
                )
            }

            // Delete Confirmation Dialog
            if (state.showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.hideDeleteDialog() },
                    title = { Text("Delete Message") },
                    text = { Text("Are you sure you want to delete this message?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                state.selectedMessageForAction?.let {
                                    viewModel.deleteMessage(it.id)
                                }
                            }
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.hideDeleteDialog() }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Edit Dialog
            if (state.showEditDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.hideEditDialog() },
                    title = { Text("Edit Message") },
                    text = {
                        EditMessageComposer(
                            content = state.editContent,
                            isNSFW = state.isNSFW,
                            isSpoiler = state.isSpoiler,
                            postingMessage = state.postingMessage,
                            characterLimit = kurozorakit.data.models.feed.message.FeedMessage.maxCharacterLimit,
                            onContentChange = viewModel::updateEditContent,
                            onNSFWToggle = viewModel::toggleNSFW,
                            onSpoilerToggle = viewModel::toggleSpoiler
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                state.selectedMessageForAction?.let {
                                    viewModel.editMessage(it)
                                }
                            },
                            enabled = state.editContent.trim().isNotEmpty() && !state.postingMessage
                        ) {
                            if (state.postingMessage) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Save")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.hideEditDialog() }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Reply Dialog
            if (state.showReplyDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.hideReplyDialog() },
                    title = { Text("Reply to Message") },
                    text = {
                        ReplyMessageComposer(
                            content = state.replyContent,
                            isNSFW = state.isNSFW,
                            isSpoiler = state.isSpoiler,
                            postingMessage = state.postingMessage,
                            characterLimit = kurozorakit.data.models.feed.message.FeedMessage.maxCharacterLimit,
                            onContentChange = viewModel::updateReplyContent,
                            onNSFWToggle = viewModel::toggleNSFW,
                            onSpoilerToggle = viewModel::toggleSpoiler
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                state.selectedMessageForAction?.let {
                                    viewModel.replyToMessage(it)
                                }
                            },
                            enabled = state.replyContent.trim().isNotEmpty() && !state.postingMessage
                        ) {
                            if (state.postingMessage) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Reply")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.hideReplyDialog() }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenNewMessageDialog(
    content: String,
    isNSFW: Boolean,
    isSpoiler: Boolean,
    postingMessage: Boolean,
    characterLimit: Int,
    onContentChange: (String) -> Unit,
    onNSFWToggle: () -> Unit,
    onSpoilerToggle: () -> Unit,
    onSendClick: () -> Unit,
    onDismiss: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }

                Text(
                    "New Message",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Invisible spacer for alignment
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Message Composer
            NewMessageComposer(
                content = content,
                isNSFW = isNSFW,
                isSpoiler = isSpoiler,
                postingMessage = postingMessage,
                characterLimit = characterLimit,
                onContentChange = onContentChange,
                onNSFWToggle = onNSFWToggle,
                onSpoilerToggle = onSpoilerToggle,
                onSendClick = {
                    onSendClick()
                    onDismiss()
                },
                focusRequester = focusRequester,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun NewMessageComposer(
    content: String,
    isNSFW: Boolean,
    isSpoiler: Boolean,
    postingMessage: Boolean,
    characterLimit: Int,
    onContentChange: (String) -> Unit,
    onNSFWToggle: () -> Unit,
    onSpoilerToggle: () -> Unit,
    onSendClick: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Content Input
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .focusRequester(focusRequester),
            placeholder = { Text("What's happening?") },
            singleLine = false,
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onSendClick() }
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Character Counter
        Text(
            text = "${content.length}/$characterLimit",
            style = MaterialTheme.typography.labelSmall,
            color = if (content.length > characterLimit) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )

        // Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Toggle Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isNSFW,
                    onClick = onNSFWToggle,
                    label = { Text("NSFW") },
                    leadingIcon = if (isNSFW) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )

                FilterChip(
                    selected = isSpoiler,
                    onClick = onSpoilerToggle,
                    label = { Text("Spoiler") },
                    leadingIcon = if (isSpoiler) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }

            // Send Button
            Button(
                onClick = onSendClick,
                enabled = content.trim().isNotEmpty() && content.length <= characterLimit && !postingMessage,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (postingMessage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun ReplyMessageComposer(
    content: String,
    isNSFW: Boolean,
    isSpoiler: Boolean,
    postingMessage: Boolean,
    characterLimit: Int,
    onContentChange: (String) -> Unit,
    onNSFWToggle: () -> Unit,
    onSpoilerToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Write your reply...") },
            singleLine = false,
            maxLines = 5,
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isNSFW,
                    onClick = onNSFWToggle,
                    label = { Text("NSFW") },
                    leadingIcon = if (isNSFW) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )

                FilterChip(
                    selected = isSpoiler,
                    onClick = onSpoilerToggle,
                    label = { Text("Spoiler") },
                    leadingIcon = if (isSpoiler) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }

            Text(
                text = "${content.length}/$characterLimit",
                style = MaterialTheme.typography.labelSmall,
                color = if (content.length > characterLimit) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EditMessageComposer(
    content: String,
    isNSFW: Boolean,
    isSpoiler: Boolean,
    postingMessage: Boolean,
    characterLimit: Int,
    onContentChange: (String) -> Unit,
    onNSFWToggle: () -> Unit,
    onSpoilerToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Edit your message...") },
            singleLine = false,
            maxLines = 5,
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isNSFW,
                    onClick = onNSFWToggle,
                    label = { Text("NSFW") },
                    leadingIcon = if (isNSFW) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )

                FilterChip(
                    selected = isSpoiler,
                    onClick = onSpoilerToggle,
                    label = { Text("Spoiler") },
                    leadingIcon = if (isSpoiler) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }

            Text(
                text = "${content.length}/$characterLimit",
                style = MaterialTheme.typography.labelSmall,
                color = if (content.length > characterLimit) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailDialog(
    message: FeedMessage,
    currentUserId: String?,
    replies: List<FeedMessage>,
    isLoadingReplies: Boolean,
    repliesError: String?,
    postingMessage: Boolean,
    windowWidth: WindowWidthSizeClass,
    onDismiss: () -> Unit,
    onReplyClick: (FeedMessage) -> Unit,
    onHeartClick: (FeedMessage) -> Unit,
    onReshareClick: (FeedMessage) -> Unit,
    onPostReply: (String) -> Unit,
    viewModel: FeedViewModel,
    modifier: Modifier = Modifier,
) {
    // Telefonlarda fullscreen, tablet/bilgisayarlarda normal dialog
    when (windowWidth) {
        WindowWidthSizeClass.COMPACT,
        WindowWidthSizeClass.MEDIUM -> {
            // Fullscreen Dialog
            FullscreenMessageDetail(
                message = message,
                currentUserId = currentUserId,
                replies = replies,
                isLoadingReplies = isLoadingReplies,
                repliesError = repliesError,
                postingMessage = postingMessage,
                onDismiss = onDismiss,
                onReplyClick = onReplyClick,
                onHeartClick = onHeartClick,
                onReshareClick = onReshareClick,
                onPostReply = onPostReply,
                viewModel = viewModel,
                modifier = modifier
            )
        }

        WindowWidthSizeClass.EXPANDED -> {
            // Normal Dialog (büyük ekranlarda)
            AlertDialog(
                onDismissRequest = onDismiss,
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.9f),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    MessageDetailContent(
                        message = message,
                        currentUserId = currentUserId,
                        replies = replies,
                        isLoadingReplies = isLoadingReplies,
                        repliesError = repliesError,
                        postingMessage = postingMessage,
                        onDismiss = onDismiss,
                        onReplyClick = onReplyClick,
                        onHeartClick = onHeartClick,
                        onReshareClick = onReshareClick,
                        onPostReply = onPostReply,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenMessageDetail(
    message: FeedMessage,
    currentUserId: String?,
    replies: List<FeedMessage>,
    isLoadingReplies: Boolean,
    repliesError: String?,
    postingMessage: Boolean,
    onDismiss: () -> Unit,
    onReplyClick: (FeedMessage) -> Unit,
    onHeartClick: (FeedMessage) -> Unit,
    onReshareClick: (FeedMessage) -> Unit,
    onPostReply: (String) -> Unit,
    viewModel: FeedViewModel,
    modifier: Modifier = Modifier
) {
    val replyContent = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversation") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // Bottom reply composer
            ReplyBottomBar(
                content = replyContent.value,
                onContentChange = { replyContent.value = it },
                postingMessage = postingMessage,
                onSendClick = {
                    if (replyContent.value.trim().isNotEmpty()) {
                        onPostReply(replyContent.value)
                        replyContent.value = ""
                    }
                },
                focusRequester = focusRequester,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Ana mesaj
            FeedMessageCard(
                feedMessage = message,
                isCurrentUser = currentUserId == message.id,
                onClick = { viewModel.showMessageDetail(message) },
                onReshareClick = onReshareClick,
                onHeartClick = onHeartClick,
                onReplyClick = onReplyClick,
                onEditClick = { viewModel.showEditDialog(it) },
                onDeleteClick = { viewModel.showDeleteDialog(it) },
                onReportClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            // Replies başlığı
            Text(
                text = "Replies (${message.attributes.metrics.replyCount})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            // Replies listesi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    isLoadingReplies -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    repliesError != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Error loading replies",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = repliesError,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadMessageReplies(message.id) }) {
                                Text("Retry")
                            }
                        }
                    }

                    replies.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Forum,
                                    contentDescription = "No replies",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    "No replies yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    "Be the first to reply!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(replies) { reply ->
                                FeedMessageCard(
                                    feedMessage = reply,
                                    isCurrentUser = currentUserId == reply.id,
                                    onClick = { viewModel.showMessageDetail(reply) },
                                    onReshareClick = onReshareClick,
                                    onHeartClick = onHeartClick,
                                    onReplyClick = onReplyClick,
                                    onEditClick = { viewModel.showEditDialog(it) },
                                    onDeleteClick = { viewModel.showDeleteDialog(it) },
                                    onReportClick = {},
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageDetailContent(
    message: FeedMessage,
    currentUserId: String?,
    replies: List<FeedMessage>,
    isLoadingReplies: Boolean,
    repliesError: String?,
    postingMessage: Boolean,
    onDismiss: () -> Unit,
    onReplyClick: (FeedMessage) -> Unit,
    onHeartClick: (FeedMessage) -> Unit,
    onReshareClick: (FeedMessage) -> Unit,
    onPostReply: (String) -> Unit,
    viewModel: FeedViewModel,
    modifier: Modifier = Modifier
) {
    val replyContent = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Conversation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ana mesaj
        FeedMessageCard(
            feedMessage = message,
            isCurrentUser = currentUserId == message.id,
            onClick = { viewModel.showMessageDetail(message) },
            onReshareClick = onReshareClick,
            onHeartClick = onHeartClick,
            onReplyClick = onReplyClick,
            onEditClick = { viewModel.showEditDialog(it) },
            onDeleteClick = { viewModel.showDeleteDialog(it) },
            onReportClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Replies başlığı
        Text(
            text = "Replies (${message.attributes.metrics.replyCount})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Replies listesi
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                isLoadingReplies -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                repliesError != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error loading replies",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = repliesError,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadMessageReplies(message.id) }) {
                            Text("Retry")
                        }
                    }
                }

                replies.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Forum,
                                contentDescription = "No replies",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                "No replies yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                "Be the first to reply!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(replies) { reply ->
                            FeedMessageCard(
                                feedMessage = reply,
                                isCurrentUser = currentUserId == reply.id,
                                onClick = { viewModel.showMessageDetail(reply) },
                                onReshareClick = onReshareClick,
                                onHeartClick = onHeartClick,
                                onReplyClick = onReplyClick,
                                onEditClick = { viewModel.showEditDialog(it) },
                                onDeleteClick = { viewModel.showDeleteDialog(it) },
                                onReportClick = {},
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Reply composer (daha küçük versiyon)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = replyContent.value,
                onValueChange = { replyContent.value = it },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = { Text("Write a reply...") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            IconButton(
                onClick = {
                    if (replyContent.value.trim().isNotEmpty()) {
                        onPostReply(replyContent.value)
                        replyContent.value = ""
                    }
                },
                enabled = replyContent.value.trim().isNotEmpty() && !postingMessage
            ) {
                if (postingMessage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Send reply")
                }
            }
        }
    }
}

@Composable
fun ReplyBottomBar(
    content: String,
    onContentChange: (String) -> Unit,
    postingMessage: Boolean,
    onSendClick: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = { Text("Write a reply...") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            IconButton(
                onClick = onSendClick,
                enabled = content.trim().isNotEmpty() && !postingMessage
            ) {
                if (postingMessage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Send reply")
                }
            }
        }
    }
}