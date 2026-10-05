@file:OptIn(ExperimentalCoroutinesApi::class)

package dunbar.mike.mediabrowser.ui.music

import dunbar.mike.mediabrowser.data.music.Band
import dunbar.mike.mediabrowser.data.music.MusicRepository
import dunbar.mike.mediabrowser.data.music.band1
import dunbar.mike.mediabrowser.data.music.band2
import dunbar.mike.mediabrowser.data.music.testDispatcher
import dunbar.mike.mediabrowser.util.ConsoleLogger
import dunbar.mike.mediabrowser.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import kotlin.time.Duration.Companion.milliseconds

class BandListViewModelTest {

    private val musicRepository: MusicRepository = mock {
        on { getBands(any(), any<Int>()) }.thenReturn(Result.success(listOf(band1, band2)))
    }
    private val logger: Logger = ConsoleLogger()

    @Test
    fun creationEmitsInitialState() = runTest(testDispatcher) {
        val viewModel = BandListViewModel(musicRepository, logger)
        assertEquals(BandListUiState(), viewModel.uiState.value)
    }

    @Test
    fun searchingWithStringLessThanFourCharactersDoesNotQueryRepository() = runTest(testDispatcher) {
        val viewModel = BandListViewModel(musicRepository, logger)
        assertEquals(BandListUiState(), viewModel.uiState.value)
        viewModel.search("G")
        viewModel.search("Gr")
        viewModel.search("Gra")
        advanceTimeBy(1000.milliseconds)
        assertEquals("Gra", viewModel.uiState.value.searchString)
        assertEquals(emptyList<Band>(), viewModel.uiState.value.bands)

    }

    @Test
    fun searchingWithStringAtLeastFourCharactersDebouncesAndQueriesRepository() = runTest(testDispatcher) {
        val viewModel = BandListViewModel(musicRepository, logger)
        advanceUntilIdle()
        assertEquals(BandListUiState(), viewModel.uiState.value)

        viewModel.search("Grat")
        assertEquals("Grat", viewModel.uiState.value.searchString)

        // Advance time past the 1-second debounce to trigger getBands()
        advanceTimeBy(1000.milliseconds)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(listOf(band1, band2), viewModel.uiState.value.bands)
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
