package dunbar.mike.musicbrowser.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dunbar.mike.musicbrowser.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicBrowserTopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Menu",
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        },
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        modifier = modifier
    )
}

@Composable
fun MusicBrowserBottomNavBar(modifier: Modifier = Modifier) {
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
            onClick = {}
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
            onClick = {}
        )
    }
}