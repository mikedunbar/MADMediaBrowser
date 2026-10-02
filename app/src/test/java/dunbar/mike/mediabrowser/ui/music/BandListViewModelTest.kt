@file:OptIn(ExperimentalCoroutinesApi::class)

package dunbar.mike.mediabrowser.ui.music

import app.cash.turbine.test
import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRepository
import dunbar.mike.mediabrowser.data.music.band1
import dunbar.mike.mediabrowser.data.music.band2
import dunbar.mike.mediabrowser.data.music.testDispatcher
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

class BandListViewModelTest {

    private val musicRepository: MusicRepository = mock {
        on { getBands(any(), any()) }.thenReturn(flowOf(listOf(band1, band2)))
    }
    private val logger: Logger = mock()

    private var viewModel = BandListViewModel(
        musicRepository = musicRepository,
        logger = logger
    )

    @Test
    fun creationEmitsInitialState() = runTest(testDispatcher) {
        assertEquals(BandListUiState(), viewModel.uiState.value)
    }

    @Test
    fun searchingWithStringLessThanFourCharactersDoesNotQueryRepository() = runTest(testDispatcher) {
        viewModel.uiState.test {
            assertEquals(BandListUiState(), awaitItem())
            viewModel.search("G")
            viewModel.search("Gr")
            viewModel.search("Gra")
            advanceTimeBy(1000)
            assertEquals("Gra", viewModel.uiState.value.searchString)
            assertEquals(emptyList<Band>(), viewModel.uiState.value.bands)
        }
    }

    @Test
    fun searchingWithStringAtLeastFourCharactersDebouncesAndQueriesRepository() = runTest(testDispatcher) {
        viewModel.uiState.test {
            assertEquals(BandListUiState(), awaitItem())
            
            viewModel.search("Grat")
            assertEquals("Grat", viewModel.uiState.value.searchString)

            advanceTimeBy(1000)

            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)

            val successState = awaitItem()
            assertEquals(false, successState.isLoading)
            assertEquals(listOf(band1, band2), successState.bands)
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
