package dunbar.mike.mediabrowser.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme

@Composable
fun PlaceholderScreen(
    screenName: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}

) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = modifier.size(20.dp))
        Text(
            text = screenName,
            style = MaterialTheme.typography.headlineLarge,
            modifier = modifier.padding(16.dp)
        )
        Spacer(modifier = modifier.size(20.dp))
        Text(
            text = "Content coming soon!",
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier.padding(16.dp)
        )
        content()
    }
}


@Preview
@Composable
fun PlaceholderScreenPreview() {
    MediaBrowserTheme {
        Surface {
            PlaceholderScreen("Placeholder Screen")
        }
    }
}