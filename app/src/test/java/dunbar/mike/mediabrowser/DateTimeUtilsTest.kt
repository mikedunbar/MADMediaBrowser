package dunbar.mike.mediabrowser

import dunbar.mike.mediabrowser.util.ZoneOffsetProvider
import dunbar.mike.mediabrowser.util.localDateTimeFromIsoInstant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateTimeUtilsTest {

    companion object {

        @JvmStatic
        fun localDataFromIsoInstantArgsProvider() = listOf(
            Arguments.of("1966-07-03T01:00:00Z", -2, LocalDateTime.of(1966, 7, 2, 23, 0, 0)),
            Arguments.of("1966-07-03T23:00:00Z", 2, LocalDateTime.of(1966, 7, 4, 1, 0, 0)),
            Arguments.of("1966-07-03T00:00:00Z", 0, LocalDateTime.of(1966, 7, 3, 0, 0, 0)),
            Arguments.of("1966-07-03T23:59:59Z", 0, LocalDateTime.of(1966, 7, 3, 23, 59, 59)),
        )
    }

    @ParameterizedTest
    @MethodSource("localDataFromIsoInstantArgsProvider")
    fun `test localDateFromIsoInstant`(
        isoDateTimeStr: String,
        localUtcOffset: Int,
        expectedLocalDate: LocalDateTime
    ) {
        // Given
        val input = isoDateTimeStr
        val offsetProvider: ZoneOffsetProvider = mock {
            on { get() } doReturn ZoneOffset.ofHours(localUtcOffset)

        }

        // When
        val localDate = localDateTimeFromIsoInstant(input, zoneOffsetProvider = offsetProvider)

        // Then
        assertEquals(expectedLocalDate, localDate)
    }

}