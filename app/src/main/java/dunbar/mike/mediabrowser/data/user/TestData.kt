package dunbar.mike.mediabrowser.data.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUserDataSource : UserDataSource {
    private val _userData = MutableStateFlow(UserData(false, DarkThemeConfig.SYSTEM_SETTING))
    override val userData: Flow<UserData> = _userData.asStateFlow()

    override suspend fun updateUserData(userData: UserData) {
        _userData.value = userData
    }

}

