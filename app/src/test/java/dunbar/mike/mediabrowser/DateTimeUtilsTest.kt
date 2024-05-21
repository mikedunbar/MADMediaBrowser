package dunbar.mike.mediabrowser

import dunbar.mike.mediabrowser.util.localDateFromIsoInstant
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateTimeUtilsTest {

    @Test
    fun `test localDateFromIsoInstant shall return correct LocalDate`() {
        val input = "1966-07-03T00:00:00Z"
        val expectedOutput = LocalDate.of(1966, 7, 3)
        assertEquals(expectedOutput, localDateFromIsoInstant(input))
    }
}