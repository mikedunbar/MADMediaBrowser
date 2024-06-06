package dunbar.mike.mediabrowser.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserDataRepo @Inject constructor(
    userDataSource: UserDataDataSource,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    val userData: Flow<UserData> = userDataSource.userData.flowOn(ioDispatcher)

}

interface UserDataDataSource {
    val userData: Flow<UserData>
}

class FakeUserDataSource : UserDataDataSource {
    override val userData: Flow<UserData> = flow {
        delay(5000)
        emit(UserData(false, DarkThemeConfig.LIGHT))
    }
}