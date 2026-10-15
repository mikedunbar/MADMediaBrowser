package dunbar.mike.mediabrowser.ui.music

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dunbar.mike.mediabrowser.ui.shared.LoadingView

@Composable
fun SongListScreenRoot(viewModel: SongListViewModel) {
    SongListScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onErrorShown = { viewModel.handleIntent(SongListIntent.ClearError) }
    )
}

@Composable
fun SongListScreen(
    uiState: SongListUiState,
    onErrorShown: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            onErrorShown()
        }
    }

    if (uiState.isLoading) {
        LoadingView()
    } else {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            uiState.songs.forEach { song ->
                Text(text = song.name)
            }
        }
    }

}

