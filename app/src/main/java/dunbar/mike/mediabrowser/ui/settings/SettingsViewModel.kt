package dunbar.mike.mediabrowser.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.user.DarkThemeConfig
import dunbar.mike.mediabrowser.data.user.UserData
import dunbar.mike.mediabrowser.data.user.UserDataRepository
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val logger: Logger
) : ViewModel() {

    private val _userDataStateFlow = userDataRepository.userData.stateIn(
        viewModelScope,
        initialValue = UserData(false, DarkThemeConfig.SYSTEM_SETTING),
        started = SharingStarted.Eagerly
    )

    val darkThemeConfig = _userDataStateFlow.map { it.darkThemeConfig }

    fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) = viewModelScope.launch {
        logger.d(TAG, "setDarkThemeConfig: $darkThemeConfig")
        userDataRepository.updateUserData(_userDataStateFlow.value.copy(darkThemeConfig = darkThemeConfig))
    }

    companion object {
        const val TAG = "SettingsViewModel"
    }
}