package dunbar.mike.mediabrowser.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dunbar.mike.mediabrowser.data.user.UserData
import dunbar.mike.mediabrowser.data.user.UserDataRepo
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepo
) : ViewModel() {

    val uiState = userDataRepository.userData.map {
        MainActivityUiState.Success(it)
    }

}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: UserData) : MainActivityUiState
}