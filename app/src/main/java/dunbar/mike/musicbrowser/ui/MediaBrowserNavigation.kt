package dunbar.mike.musicbrowser.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dunbar.mike.musicbrowser.ui.music.AlbumListScreen
import dunbar.mike.musicbrowser.ui.music.AlbumListViewModel
import dunbar.mike.musicbrowser.ui.music.BandListScreen
import dunbar.mike.musicbrowser.ui.music.MusicLibraryScreen
import dunbar.mike.musicbrowser.ui.music.SongListScreen
import dunbar.mike.musicbrowser.ui.settings.AboutScreen
import dunbar.mike.musicbrowser.ui.settings.SettingsScreen
import dunbar.mike.musicbrowser.ui.video.VideoLibraryScreen

@Composable
// TODO Get this showing up, using new M3 style
fun NavDrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Header", fontSize = 60.sp)
    }
}

@Composable
fun NavDrawerBody(
    items: List<NavDrawerItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (NavDrawerItem) -> Unit,
) {
    // TODO use standard nav drawer, when in landscape phone or larger screen
    ModalDrawerSheet {
        LazyColumn(modifier) {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onItemClick(item)
                        }
                        .padding(16.dp)
                ) {
                    Icon(imageVector = item.selectedIcon, contentDescription = item.contentDescription)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = item.title, modifier = Modifier.weight(1f), style = itemTextStyle)
                }
            }
        }
    }
}

data class NavDrawerItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val contentDescription: String,
)

val navDrawerItemList = listOf(
    NavDrawerItem(
        route = Screen.Settings.name,
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        contentDescription = "Settings"
    ),
    NavDrawerItem(
        route = Screen.About.name,
        title = "About This App",
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info,
        contentDescription = "Settings"
    )

)

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
                onClickBand = { bandName ->
                    logger.d("MusicBrowserNavHost", "Band was clicked: $bandName")
                    navController.navigate("${Screen.AlbumList.name}/$bandName")
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
        composable(Screen.Settings.name) {
            SettingsScreen()
        }
        composable(Screen.About.name) {
            AboutScreen()
        }
    }
}

enum class Screen {
    Home,
    Settings,
    About,
    MusicLibrary,
    BandList,
    AlbumList,
    SongList,
    VideoLibrary,

    ;
}