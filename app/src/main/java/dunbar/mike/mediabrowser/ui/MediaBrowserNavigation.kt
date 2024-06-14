package dunbar.mike.mediabrowser.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dunbar.mike.mediabrowser.R
import dunbar.mike.mediabrowser.ui.music.AlbumListScreenRoot
import dunbar.mike.mediabrowser.ui.music.AlbumListViewModel
import dunbar.mike.mediabrowser.ui.music.BandListScreenRoot
import dunbar.mike.mediabrowser.ui.music.MusicLibraryScreen
import dunbar.mike.mediabrowser.ui.settings.SettingsScreenRoot

@Composable
fun NavDrawerBody(
    currentNavDestination: NavDestination?,
    items: List<NavigationItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (NavigationItem) -> Unit,
) {
    ModalDrawerSheet {
        LazyColumn(modifier) {
            items(items) { item ->
                val currentRoute = currentNavDestination?.route ?: Screen.Home.name
                val itemScreen = Screen.valueOf(item.route)
                val selected = itemScreen.contains(currentRoute)
                val icon = if (selected) item.selectedIcon else item.unselectedIcon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onItemClick(item)
                        }
                        .padding(16.dp)
                ) {
                    Icon(imageVector = icon, contentDescription = item.title)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = item.title, modifier = Modifier.weight(1f), style = itemTextStyle)
                }
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val navigationDrawerItemLists = listOf(
    NavigationItem(
        route = Screen.Settings.name,
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    ),
    NavigationItem(
        route = Screen.About.name,
        title = "About This App",
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info,
    )

)

@Composable
fun MediaBrowserNavHost(
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
            BandListScreenRoot(
                onClickBand = { bandId ->
                    navController.navigate("${Screen.AlbumList.name}/$bandId")
                },
            )
        }
        composable("${Screen.AlbumList.name}/{bandId}") {
            val bandId = it.arguments?.getString("bandId") ?: "GratefulDead"
            val albumListViewModel = hiltViewModel<AlbumListViewModel>()
            albumListViewModel.setBand(bandId)
            AlbumListScreenRoot(albumListViewModel)
        }
        composable(Screen.SongList.name) {
            PlaceholderScreen(screenName = stringResource(id = R.string.songs_screen))
        }
        composable(Screen.VideoLibrary.name) {
            PlaceholderScreen(screenName = stringResource(id = R.string.video_library))
        }
        composable(Screen.Settings.name) {
            SettingsScreenRoot()
        }
        composable(Screen.About.name) {
            PlaceholderScreen(screenName = stringResource(id = R.string.about_screen))
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

    fun contains(route: String): Boolean {
        val doesContain = when (this) {
            MusicLibrary -> route.startsWith(MusicLibrary.name) || route.startsWith(BandList.name) || route.startsWith(AlbumList.name) || route.startsWith(
                SongList.name
            )

            else -> route.startsWith(this.name)
        }
        return doesContain
    }
}