package dunbar.mike.mediabrowser.ui

import androidx.compose.runtime.staticCompositionLocalOf
import dunbar.mike.mediabrowser.util.AndroidLogger
import dunbar.mike.mediabrowser.util.Logger

val LocalLogger = staticCompositionLocalOf<Logger> { AndroidLogger() }
