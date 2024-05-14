package dunbar.mike.musicbrowser.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import dunbar.mike.musicbrowser.R
import dunbar.mike.musicbrowser.ui.theme.MediaBrowserTheme

@Composable
fun MediaBrowserApp() {
    MediaBrowserTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        // TODO get the nav drawer working: settings, about this app, etc.
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                // drawer items go here
            }
        ) {
            // screen content goes here
        }

        val navController = rememberNavController()

        val onClickHome = {
            navController.navigate(Screen.Home.name)
        }

        val onClickMusic = {
            navController.navigate(Screen.MusicLibrary.name)
        }

        val onClickVideo = {
            navController.navigate(Screen.VideoLibrary.name)
        }

        Scaffold(
            topBar = { MediaBrowserTopAppBar() },
            bottomBar = { MediaBrowserBottomNavBar(onClickHome, onClickMusic, onClickVideo) }
        ) { paddingValues ->
            Surface {
                MusicBrowserNavHost(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaBrowserTopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Menu",
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        },
        title = {
            // TODO Make this dynamic, based on current screen
            Text(text = stringResource(R.string.app_name))
        },
        modifier = modifier
    )
}

@Composable
fun MediaBrowserBottomNavBar(
    onClickHome: () -> Unit,
    onClickMusic: () -> Unit,
    onClickVideo: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.LibraryMusic,
                    contentDescription = stringResource(R.string.music_library)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.music_library)
                )
            },
            selected = true,
            onClick = onClickMusic
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(R.string.home)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.home)
                )
            },
            selected = true,
            onClick = onClickHome
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = stringResource(R.string.video_library)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.video_library)
                )
            },
            selected = true,
            onClick = onClickVideo
        )
    }
}