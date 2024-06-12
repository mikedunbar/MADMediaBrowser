package dunbar.mike.mediabrowser.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Loading...")
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun ErrorView(message: String) {
    Column()
    {
        Text(text = "Error Encountered", fontSize = MaterialTheme.typography.titleLarge.fontSize)
        Text(text = message, fontStyle = FontStyle.Italic)
    }

}

@PreviewLightDark
@Composable
private fun LoadingPreview() {
    MediaBrowserTheme {
        Surface {
            LoadingView()
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun ErrorPreview() {
    MediaBrowserTheme {
        Surface {
            ErrorView("You appear to be offline")
        }
    }
}