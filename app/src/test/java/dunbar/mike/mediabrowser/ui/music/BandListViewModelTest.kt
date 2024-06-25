@file:OptIn(ExperimentalCoroutinesApi::class)

package dunbar.mike.mediabrowser.ui.music

import app.cash.turbine.test
import dunbar.mike.mediabrowser.data.music.MusicRepository
import dunbar.mike.mediabrowser.data.music.testDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock

class BandListViewModelTest {

    private val mockRepository: MusicRepository = mock {
        onBlocking { it.getBands(any(), any()) } doSuspendableAnswer {
            delay(100)
            Result.success(emptyList())
        }
    }

    private val failingMockRepository: MusicRepository = mock {
        onBlocking { it.getBands(any(), any()) } doSuspendableAnswer {
            delay(100)
            Result.failure(Exception("Search Failed"))
        }
    }

    private var viewModel = BandListViewModel(mockRepository)

    @Test
    fun creationEmitsInitialUiState() = runTest(testDispatcher) {
        assertEquals(BandListUiState.Initial, viewModel.uiState.value)
    }

    @Test
    fun searchingInInitialUiStateEmitsLoadingFollowedBySuccessForSuccessfulSearch() = runTest(testDispatcher) {
        viewModel.uiState.test {
            assertEquals(BandListUiState.Initial, awaitItem())
            viewModel.search("Grateful")
            assertEquals(BandListUiState.Loading, awaitItem())
            assertEquals(BandListUiState.Success(page = 1, bands = emptyList()), awaitItem())
        }
    }

    @Test
    fun searchingInInitialUiStateEmitsLoadingFollowedByErrorForFailedSearch() = runTest(testDispatcher) {
        viewModel = BandListViewModel(failingMockRepository)

        viewModel.uiState.test {
            assertEquals(BandListUiState.Initial, awaitItem())
            viewModel.search("Grateful")
            assertEquals(BandListUiState.Loading, awaitItem())
            assertEquals(BandListUiState.Error(message = "Search Failed"), awaitItem())
        }
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            Dispatchers.setMain(testDispatcher)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            Dispatchers.resetMain()
        }
    }
}