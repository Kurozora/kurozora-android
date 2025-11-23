package com.seloreis.kurozora.ui.screens.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kurozoraapp.composeapp.generated.resources.Res
import org.openani.mediamp.compose.MediampPlayerSurface
import org.openani.mediamp.compose.rememberMediampPlayer
import org.openani.mediamp.playUri

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
) {
    val titles = listOf("watchers", "readers", "listeners", "players", "fans")
    var currentIndex by remember { mutableIntStateOf(0) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while(true) {
            delay(2000)
            visible = false
            delay(300)
            currentIndex = (currentIndex + 1) % titles.size
            visible = true
        }
    }

    val player = rememberMediampPlayer()
    LaunchedEffect(player) {
        player.playUri("https://kurozora.app/videos/hero_video.mov")
    }

    var appIcon: ByteArray? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        appIcon = Res.readBytes("files/static/icon/app_icon.png")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ------------------ VIDEO BACKGROUND ------------------
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Black)
        ) {
            MediampPlayerSurface(player, Modifier.fillMaxSize())
        }

        // ------------------ LOGO + ANIMATED TEXT + BUTTON OVERLAY ------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Üst logo
            appIcon?.decodeToImageBitmap()?.let {
//                Image(
//                    bitmap = it,
//                    contentDescription = "Logo",
//                    modifier = Modifier.size(120.dp)
//                )
            }

            Spacer(Modifier.height(32.dp))

            // Animasyonlu text
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Calling all ",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AnimatedVisibility(visible = visible) {
                    Text(
                        text = titles[currentIndex],
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp)
            ) {
                Text("Get Started")
            }
        }

        // ------------------ ALT LOGO + DESCRIPTION ------------------
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter("file:///android_asset/images/footer_logo.png"),
                contentDescription = "Footer Logo",
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "The best platform for anime, manga and more.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}