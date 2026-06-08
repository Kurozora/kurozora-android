package app.kurozora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.kurozora.ui.screens.main.MainScreen
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        //enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        FileKit.init(this)

        setContent {
            //App()
            MainScreen()
        }
    }
}
