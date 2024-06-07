package dunbar.mike.mediabrowser.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dunbar.mike.mediabrowser.data.user.DarkThemeConfig
import dunbar.mike.mediabrowser.ui.theme.MediaBrowserTheme
import dunbar.mike.mediabrowser.util.AndroidLogger
import dunbar.mike.mediabrowser.util.Logger

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>(),
    logger: Logger = AndroidLogger,
) {
    val currentConfig = viewModel.darkThemeConfig.collectAsStateWithLifecycle(initialValue = DarkThemeConfig.SYSTEM_SETTING).value
    logger.d("SettingsScreenRoot", "currentConfig: $currentConfig")
    val currentOption = DarkThemeSettingsOption.fromConfig(currentConfig)

    SettingsScreen(
        darkThemeSettingsOption = currentOption,
        onSelected = {
            viewModel.setDarkThemeConfig(DarkThemeSettingsOption.toConfig(it))
        }
    )
}

@Composable
fun SettingsScreen(
    darkThemeSettingsOption: DarkThemeSettingsOption,
    onSelected: (DarkThemeSettingsOption) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Settings Screen",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        DarkThemeSetting(darkThemeSettingsOption, onSelected)
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    MediaBrowserTheme {
        Surface {
            SettingsScreen(
                darkThemeSettingsOption = DarkThemeSettingsOption.SystemDefault,
                onSelected = {},
            )
        }
    }
}

@Composable
fun DarkThemeSetting(
    current: DarkThemeSettingsOption,
    onSelected: (DarkThemeSettingsOption) -> Unit = {}
) {
    // Create a list of dark mode options
    val darkThemeSettingsOptions = listOf(
        DarkThemeSettingsOption.SystemDefault, DarkThemeSettingsOption.Light, DarkThemeSettingsOption.Dark
    )
    // Create a Column to display the dark mode options
    Column {
        // Display the header
        Text(
            text = "Dark Mode", style = MaterialTheme.typography.headlineSmall
        )

        // Display the list of dark mode options
        LazyColumn {
            items(darkThemeSettingsOptions) { option ->
                // Create a Row for each option
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelected(option)
                    }
                    .padding(16.dp)) {
                    // Display the option name
                    Text(
                        text = option.desc, style = MaterialTheme.typography.bodySmall
                    )

                    // Display a checkmark if the option is selected
                    if (current == option) {
                        Icon(
                            imageVector = Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

enum class DarkThemeSettingsOption(val desc: String) {
    SystemDefault("System Default"),
    Light("Light"),
    Dark("Dark")

    ;

    companion object {
        fun fromConfig(model: DarkThemeConfig): DarkThemeSettingsOption =
            when (model) {
                DarkThemeConfig.SYSTEM_SETTING -> SystemDefault
                DarkThemeConfig.LIGHT -> Light
                DarkThemeConfig.DARK -> Dark
            }

        fun toConfig(option: DarkThemeSettingsOption): DarkThemeConfig =
            when (option) {
                SystemDefault -> DarkThemeConfig.SYSTEM_SETTING
                Light -> DarkThemeConfig.LIGHT
                Dark -> DarkThemeConfig.DARK
            }
    }

}