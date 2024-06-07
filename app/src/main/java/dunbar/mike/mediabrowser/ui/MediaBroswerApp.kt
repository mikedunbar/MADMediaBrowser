package dunbar.mike.mediabrowser.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dunbar.mike.mediabrowser.R
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme
import kotlinx.coroutines.launch

@Composable
fun MediaBrowserApp(darkTheme: Boolean = false) {
    MediaBrowserTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()

        val onNavDrawerItemClick = { item: NavigationItem ->
            coroutineScope.launch { drawerState.close() }
            navController.navigate(item.route)
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NavDrawerBody(
                    currentNavDestination = navController.currentBackStackEntryAsState().value?.destination,
                    items = navigationDrawerItemLists,
                    onItemClick = onNavDrawerItemClick
                )
            }
        ) {
            Scaffold(
                topBar = {
                    MediaBrowserTopAppBar(
                        onNavigationIconClick = {
                            coroutineScope.launch { drawerState.open() }
                        })
                },
                bottomBar = {
                    MediaBrowserBottomNavBar(
                        navController = navController,
                        items = listOf(
                            NavigationItem(
                                route = Screen.MusicLibrary.name,
                                title = stringResource(R.string.music_library),
                                selectedIcon = Icons.Filled.LibraryMusic,
                                unselectedIcon = Icons.Outlined.LibraryMusic,
                            ),
                            NavigationItem(
                                route = Screen.Home.name,
                                title = stringResource(R.string.home),
                                selectedIcon = Icons.Filled.Home,
                                unselectedIcon = Icons.Outlined.Home,
                            ),
                            NavigationItem(
                                route = Screen.VideoLibrary.name,
                                title = stringResource(R.string.video_library),
                                selectedIcon = Icons.Filled.VideoLibrary,
                                unselectedIcon = Icons.Outlined.VideoLibrary,
                            ),
                        ),
                    )
                }
            ) { paddingValues ->
                Surface {
                    MediaBrowserNavHost(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaBrowserTopAppBar(
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

        },
        title = {
            // TODO Make this dynamic, based on current screen
            Text(text = stringResource(R.string.app_name))
        },
        modifier = modifier,
    )
}

@Composable
fun MediaBrowserBottomNavBar(
    navController: NavController,
    items: List<NavigationItem>,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        items.forEach {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: Screen.Home.name
            val itemScreen = Screen.valueOf(it.route)
            val selected = itemScreen.contains(currentRoute)
            val imageVector = if (selected) it.selectedIcon else it.unselectedIcon
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = it.title
                    )
                },
                label = {
                    Text(
                        text = it.title
                    )
                },
                selected = selected,
                onClick = { navController.navigate(it.route) }
            )
        }
    }
}