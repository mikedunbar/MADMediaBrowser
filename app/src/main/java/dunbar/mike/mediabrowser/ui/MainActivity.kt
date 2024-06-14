package dunbar.mike.mediabrowser.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dunbar.mike.mediabrowser.data.user.DarkThemeConfig.DARK
import dunbar.mike.mediabrowser.data.user.DarkThemeConfig.LIGHT
import dunbar.mike.mediabrowser.data.user.DarkThemeConfig.SYSTEM_SETTING
import dunbar.mike.mediabrowser.ui.MainActivityUiState.Loading
import dunbar.mike.mediabrowser.ui.MainActivityUiState.Success
import dunbar.mike.mediabrowser.util.AndroidLogger
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    private val logger = AndroidLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        logger.d(TAG, "onCreate: installed splash screen, holding until minimal user data loaded")

        var uiState: MainActivityUiState by mutableStateOf(Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(STARTED) {
                viewModel.uiState.collect {
                    uiState = it
                    logger.d(TAG, "onCreate: collected uiState = $uiState")
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                Loading -> true
                is Success -> false
            }
        }

        setContent {
            MediaBrowserApp(shouldUseDarkTheme(uiState))
        }
    }

    companion object {
        private const val TAG = "MainActivity"
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



