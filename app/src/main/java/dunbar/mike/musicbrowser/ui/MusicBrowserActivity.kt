package dunbar.mike.musicbrowser.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dunbar.mike.musicbrowser.ui.theme.MusicBrowserTheme
import dunbar.mike.musicbrowser.util.AndroidLogger

val logger = AndroidLogger

@AndroidEntryPoint
class MusicBrowserActivity : ComponentActivity() {
    private val bandListViewModel by viewModels<BandListViewModel>()
    private val albumListViewModel by viewModels<AlbumListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicBrowserApp(
                bandListViewModel,
                albumListViewModel,
            )
        }
    }
}

@Composable
fun MusicBrowserApp(
    bandListViewModel: BandListViewModel,
    albumListViewModel: AlbumListViewModel,
) {

    MusicBrowserTheme {
        val navController = rememberNavController()
        Scaffold(
            topBar = { AppBar() }
        ) { paddingValues ->

            Surface {
                MusicBrowserNavHost(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues),
                    bandListViewModel = bandListViewModel,
                    albumListViewModel = albumListViewModel
                )
            }
        }
    }
}


@Composable
fun MusicBrowserNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    bandListViewModel: BandListViewModel,
    albumListViewModel: AlbumListViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Bands.name,
        modifier = modifier,
    ) {
        composable(Screen.Bands.name) {
            BandsScreenBody(
                logger,
                viewModel = bandListViewModel,
                onClickBand = {
                    logger.d("MusicBrowserNavHost", "Band was clicked: $it")
                    navController.navigate("${Screen.Albums.name}/$it")
                },
            )
        }
        composable("${Screen.Albums.name}/{bandName}") {
            val bandName = it.arguments?.getString("bandName") ?: "Grateful Dead"
            albumListViewModel.setBand(bandName)
            AlbumScreenBody(
                viewModel = albumListViewModel,

                )
        }
    }
}
