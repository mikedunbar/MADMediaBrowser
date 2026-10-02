package dunbar.mike.mediabrowser.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dunbar.mike.mediabrowser.data.user.DarkThemeConfig.DARK
import dunbar.mike.mediabrowser.data.user.DarkThemeConfig.LIGHT
import dunbar.mike.mediabrowser.data.user.DarkThemeConfig.SYSTEM_SETTING
import dunbar.mike.mediabrowser.ui.MainActivityUiState.Loading
import dunbar.mike.mediabrowser.ui.MainActivityUiState.Success
import dunbar.mike.mediabrowser.util.Logger
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val logTag = "MainActivity"
    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        logger.d(logTag, "onCreate: installed splash screen, holding until minimal user data loaded")

        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value is Loading
        }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalLogger provides logger) {
                MediaBrowserApp(shouldUseDarkTheme(uiState))
            }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    Loading -> isSystemInDarkTheme()
    is Success -> when (uiState.userData.darkThemeConfig) {
        SYSTEM_SETTING -> isSystemInDarkTheme()
        LIGHT -> false
        DARK -> true
    }
}



