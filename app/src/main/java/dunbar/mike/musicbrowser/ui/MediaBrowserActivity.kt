package dunbar.mike.musicbrowser.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dunbar.mike.musicbrowser.ui.music.AlbumListViewModel
import dunbar.mike.musicbrowser.ui.music.BandListViewModel
import dunbar.mike.musicbrowser.util.AndroidLogger

val logger = AndroidLogger

@AndroidEntryPoint
class MediaBrowserActivity : ComponentActivity() {
    //TODO get this out of here
    private val bandListViewModel by viewModels<BandListViewModel>()
    private val albumListViewModel by viewModels<AlbumListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            MediaBrowserApp(
                bandListViewModel,
                albumListViewModel,
            )
        }
    }
}



