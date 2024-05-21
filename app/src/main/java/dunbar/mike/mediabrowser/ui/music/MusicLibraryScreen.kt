package dunbar.mike.mediabrowser.ui.music

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MusicLibraryScreen(onBandListClick: () -> Unit) {
    Column {
        Text("Music Library Screen")
        Button(
            onClick = onBandListClick
        ) {
            Text("Go to Band List")
        }
    }
}