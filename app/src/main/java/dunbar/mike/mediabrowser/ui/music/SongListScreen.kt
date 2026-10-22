package dunbar.mike.mediabrowser.ui.music

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dunbar.mike.mediabrowser.ui.shared.LoadingView
import java.util.Locale

@Composable
fun SongListScreenRoot(viewModel: SongListViewModel) {
    SongListScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onIntent = { viewModel.handleIntent(it) }
    )
}

@Composable
fun SongListScreen(
    uiState: SongListUiState,
    onIntent: (SongListIntent) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            onIntent(SongListIntent.ClearError)
        }
    }

    if (uiState.isLoading) {
        LoadingView()
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 130.dp)
            ) {
                uiState.songs.forEach { song ->
                    val isCurrent = uiState.currentPlayingSong == song
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { onIntent(SongListIntent.PlaySong(song)) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isCurrent && uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = song.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            uiState.currentPlayingSong?.let { playingSong ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Now Playing",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = playingSong.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1
                                )
                            }
                            IconButton(onClick = { onIntent(SongListIntent.PlayPrevious) }) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Song")
                            }
                            IconButton(onClick = { onIntent(SongListIntent.SeekBackward) }) {
                                Icon(Icons.Default.Replay10, contentDescription = "Rewind 10s")
                            }
                            IconButton(onClick = { onIntent(SongListIntent.TogglePlayPause) }) {
                                Icon(
                                    imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle Play/Pause"
                                )
                            }
                            IconButton(onClick = { onIntent(SongListIntent.SeekForward) }) {
                                Icon(Icons.Default.Forward10, contentDescription = "Forward 10s")
                            }
                            IconButton(onClick = { onIntent(SongListIntent.PlayNext) }) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Next Song")
                            }
                        }

                        val duration = if (uiState.durationMs > 0) uiState.durationMs.toFloat() else 1f
                        val position = uiState.currentPositionMs.toFloat().coerceIn(0f, duration)

                        Slider(
                            value = position,
                            onValueChange = { onIntent(SongListIntent.SeekTo(it.toLong())) },
                            valueRange = 0f..duration,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = formatMillis(uiState.currentPositionMs),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = formatMillis(uiState.durationMs),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatMillis(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}
