package dunbar.mike.mediabrowser.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dunbar.mike.mediabrowser.R
import dunbar.mike.mediabrowser.ui.shared.PlaceholderScreen
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme


@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(screenName = stringResource(id = R.string.home_screen)) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(20.dp)
        ) {
            Text(text = stringResource(id = R.string.home_screen_disclaimer))
        }

    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    MediaBrowserTheme {
        Surface {
            HomeScreen()
        }
    }
}