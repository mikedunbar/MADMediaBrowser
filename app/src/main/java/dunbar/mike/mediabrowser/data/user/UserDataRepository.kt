package dunbar.mike.mediabrowser.data.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dunbar.mike.mediabrowser.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.userPrefDataStore: DataStore<Preferences> by preferencesDataStore(name = "UserSettings")

interface UserDataSource {
    val userData: Flow<UserData>

    suspend fun updateUserData(userData: UserData)
}

class RealUserDataSource(
    private val context: Context
) : UserDataSource {


    override val userData: Flow<UserData> = context.userPrefDataStore.data.map { prefs ->
        val darkThemeConfig = when (prefs[DARK_THEME_KEY] ?: DarkThemeConfig.LIGHT.name) {
            DarkThemeConfig.LIGHT.name -> DarkThemeConfig.LIGHT
            DarkThemeConfig.DARK.name -> DarkThemeConfig.DARK
            else -> DarkThemeConfig.SYSTEM_SETTING
        }
        UserData(false, darkThemeConfig)
    }

    override suspend fun updateUserData(userData: UserData) {
        context.userPrefDataStore.edit { prefs ->
            prefs[DARK_THEME_KEY] = userData.darkThemeConfig.name
        }
    }

    companion object {
        private val DARK_THEME_KEY = stringPreferencesKey("dark_theme")
    }

}

class UserDataRepository @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    val userData: Flow<UserData> = userDataSource.userData.flowOn(ioDispatcher)

    suspend fun updateUserData(userData: UserData) {
        userDataSource.updateUserData(userData)
    }

}