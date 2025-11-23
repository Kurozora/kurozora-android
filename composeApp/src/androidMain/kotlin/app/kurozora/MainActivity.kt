package app.kurozora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.kurozora.ui.screens.main.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        //enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            //App()
            MainScreen()
        }
    }
}
