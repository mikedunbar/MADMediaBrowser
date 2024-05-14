package dunbar.mike.musicbrowser.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dunbar.mike.musicbrowser.ui.music.AlbumListScreen
import dunbar.mike.musicbrowser.ui.music.AlbumListViewModel
import dunbar.mike.musicbrowser.ui.music.BandListScreen
import dunbar.mike.musicbrowser.ui.music.MusicLibraryScreen
import dunbar.mike.musicbrowser.ui.music.SongListScreen

@Composable
fun MusicBrowserNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.name,
        modifier = modifier,
    ) {
        composable(Screen.Home.name) {
            HomeScreen()
        }
        composable(Screen.MusicLibrary.name) {
            MusicLibraryScreen(onBandListClick = { navController.navigate(Screen.BandList.name) })
        }
        composable(Screen.BandList.name) {
            BandListScreen(
                logger,
                onClickBand = {
                    logger.d("MusicBrowserNavHost", "Band was clicked: $it")
                    navController.navigate("${Screen.AlbumList.name}/$it")
                },
            )
        }
        composable("${Screen.AlbumList.name}/{bandName}") {
            val bandName = it.arguments?.getString("bandName") ?: "Grateful Dead"
            val albumListViewModel = hiltViewModel<AlbumListViewModel>()
            albumListViewModel.setBand(bandName)
            AlbumListScreen(albumListViewModel)
        }
        composable(Screen.SongList.name) {
            SongListScreen()
        }
        composable(Screen.VideoLibrary.name) {
            VideoLibraryScreen()
        }
    }
}

enum class Screen {
    Home,
    MusicLibrary,
    BandList,
    AlbumList,
    SongList,
    VideoLibrary,

    ;
}