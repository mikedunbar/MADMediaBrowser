package dunbar.mike.mediabrowser.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dunbar.mike.mediabrowser.R
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme


@Composable
fun PlaceholderScreen(
    screenName: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}

) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = screenName,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = "Content coming soon!",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
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

@Composable
fun StubScreen(
    @StringRes title: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineLarge,
            modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        SearchBar(Modifier.padding(horizontal = 16.dp))
        ContentSection(title = R.string.trending_videos) {
            MediaCollectionGrid()
        }
        ContentSection(title = R.string.trending_music) {
            MediaCollectionGrid()
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun StubScreenPreview() {
    MediaBrowserTheme {
        Surface {
            StubScreen(R.string.home_screen)
        }
    }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier
) {
    TextField(
        value = "",
        onValueChange = {},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        placeholder = {
            Text(stringResource(id = R.string.placeholder_search))
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(56.dp)

    )
}

@Composable
fun MediaCollectionGrid(
    modifier: Modifier = Modifier
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.height(168.dp)
    ) {
        items(mediaItemsData) { item ->
            MediaItemCard(
                drawableId = item.drawable, stringId = item.text, modifier = Modifier.height(80.dp)
            )
        }
    }
}

@Composable
fun MediaItemCard(
    modifier: Modifier = Modifier,
    @DrawableRes drawableId: Int,
    @StringRes stringId: Int
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(255.dp)
        ) {
            Image(
                painter = painterResource(drawableId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp),
            )
            Text(
                text = stringResource(stringId),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

private val mediaItemsData = listOf(
    R.drawable.sample_vector_bird to R.string.lorem_ipsum,
    R.drawable.sample_vector_bird to R.string.lorem_ipsum,
    R.drawable.sample_vector_bird to R.string.lorem_ipsum,
    R.drawable.sample_vector_bird to R.string.lorem_ipsum,
    R.drawable.sample_vector_bird to R.string.lorem_ipsum,
).map { DrawableStringPair(it.first, it.second) }

private data class DrawableStringPair(
    @DrawableRes val drawable: Int,
    @StringRes val text: Int
)

@Composable
fun ContentSection(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp)
        )
        content()
    }
}