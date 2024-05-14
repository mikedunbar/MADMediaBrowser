package dunbar.mike.musicbrowser.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dunbar.mike.musicbrowser.util.AndroidLogger

val logger = AndroidLogger

@AndroidEntryPoint
class MediaBrowserActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            MediaBrowserApp()
        }
    }
}



