package dunbar.mike.mediabrowser.ui.music

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dunbar.mike.mediabrowser.R
import dunbar.mike.mediabrowser.ui.shared.PlaceholderScreen

@Composable
fun MusicLibraryScreen(onBandListClick: () -> Unit) {
    PlaceholderScreen(screenName = stringResource(id = R.string.music_library_screen)) {
        Button(
            onClick = onBandListClick
        ) {
            Text("Go to Band List")
        }
    }
}