@file:OptIn(ExperimentalCoroutinesApi::class)

package dunbar.mike.mediabrowser.ui.music

import app.cash.turbine.test
import dunbar.mike.mediabrowser.data.music.Band
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
import org.mockito.kotlin.KInvocationOnMock
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import dunbar.mike.mediabrowser.data.music.band1
import dunbar.mike.mediabrowser.data.music.band2

class BandListViewModelTest {

    private var viewModel = BandListViewModel(
        createMockRepository {
            delay(100)
            Result.success(listOf(band1, band2))
        }
    )

    @Test
    fun creationEmitsInitialUiState() = runTest(testDispatcher) {
        assertEquals(BandListUiState.Initial, viewModel.uiState.value)
    }

    @Test
    fun searchingDoesNotQueryRepositoryAndEmitsNothingWhenSearchStringLessThanFourCharacters() = runTest(testDispatcher) {
        viewModel.uiState.test {
            assertEquals(BandListUiState.Initial, awaitItem())
            viewModel.search("G")
            viewModel.search("Gr")
            viewModel.search("Gra")
            // No additional items emitted yet, after BandListUiState.Initial
            viewModel.search("Grat")
            assertEquals(BandListUiState.Loading, awaitItem())
            assertEquals(BandListUiState.Success(page = 1, bands = listOf(band1, band2)), awaitItem())
            viewModel.search("Gra")
            viewModel.search("Gr")
            // No emissions here either, simulating delete text in search bar
        }
    }

    @Test
    fun searchingInInitialUiStateEmitsLoadingFollowedBySuccessForSuccessfulSearch() = runTest(testDispatcher) {
        viewModel.uiState.test {
            assertEquals(BandListUiState.Initial, awaitItem())
            viewModel.search("Grateful")
            assertEquals(BandListUiState.Loading, awaitItem())
            assertEquals(BandListUiState.Success(page = 1, bands = listOf(band1, band2)), awaitItem())
        }
    }

    @Test
    fun searchingInInitialUiStateEmitsLoadingFollowedByErrorForFailedSearch() = runTest(testDispatcher) {
        viewModel = BandListViewModel(
            createMockRepository {
                delay(100)
                Result.failure(Exception("Search Failed"))
            }
        )

        viewModel.uiState.test {
            assertEquals(BandListUiState.Initial, awaitItem())
            viewModel.search("Grateful")
            assertEquals(BandListUiState.Loading, awaitItem())
            assertEquals(BandListUiState.Error(message = "Search Failed"), awaitItem())
        }
    }

    @Test
    fun searchingInStateOtherThanInitialMaintainsCurrentStateThenEmitsSuccessForSuccessfulSearch() = runTest(testDispatcher) {
        viewModel.uiState.test {
            // Drive to loading state
            assertEquals(BandListUiState.Initial, awaitItem())
            viewModel.search("Grateful")
            assertEquals(BandListUiState.Loading, awaitItem())

            // Search again before delay in mock completes
            viewModel.search("The Beatles")
            assertEquals(BandListUiState.Success(page = 1, bands = listOf(band1, band2)), awaitItem())
        }
    }

    @Test
    fun searchingInStateOtherThanInitialMaintainsCurrentStateThenEmitsErrorForFailedSearch() = runTest(testDispatcher) {
        viewModel = BandListViewModel(
            createMockRepository {
                delay(100)
                Result.failure(Exception("Search Failed"))
            }
        )

        viewModel.uiState.test {
            // Drive to loading state
            assertEquals(BandListUiState.Initial, awaitItem())
            viewModel.search("Grateful")
            assertEquals(BandListUiState.Loading, awaitItem())

            // Search again before delay in mock completes
            viewModel.search("The Beatles")
            assertEquals(BandListUiState.Error("Search Failed"), awaitItem())
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

private fun createMockRepository(
    getBandsAnswer: suspend (KInvocationOnMock) -> Result<List<Band>>
): MusicRepository {
    return mock {
        onBlocking { it.getBands(any(), any()) } doSuspendableAnswer getBandsAnswer
    }
}