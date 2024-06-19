package dunbar.mike.mediabrowser.util

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LoggerTest {
    private val loggerUnderTest: ConsoleLogger = ConsoleLogger()

    // Intentionally loose / only care about rough format - a datetime stamp followed by the log message
    private val logMessagePattern = Regex(
        """\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d:\d\d?\d?.*"""
    )

    //TODO Parameterize these
    @Test
    fun `test debug logging`() {
        // given
        val testTag = "TEST_TAG"
        val logMsgInput = "Name=%s. Age=%d."

        // when
        loggerUnderTest.d(testTag, logMsgInput, "Mike", 46)

        val logMsg = loggerUnderTest.logMsgs[0]

        // then
        assertTrue(logMessagePattern.matches(logMsg))
        assertTrue(logMsg.endsWith("D: TEST_TAG Name=Mike. Age=46."))
    }

    @Test
    fun `test error logging`() {
        // given
        val testTag = "TEST_TAG"
        val logMsgInput = "Name: %s. Age: %d."

        // when
        loggerUnderTest.e(testTag, logMsgInput, "Mike", 46)

        val logMsg = loggerUnderTest.logMsgs[0]

        // then
        assertTrue(logMessagePattern.matches(logMsg))
        assertTrue(logMsg.endsWith("E: TEST_TAG Name: Mike. Age: 46."))
    }

    @Test
    fun `test log list`() {
        //given
        val testTag = "TEST_TAG"
        val logMsgInput = "Cat List=%s"

        // when
        loggerUnderTest.d(testTag, logMsgInput, listOf("Tiger", "Eeyore", "Abby"))

        val logMsg = loggerUnderTest.logMsgs[0]

        // then
        assertTrue(logMessagePattern.matches(logMsg))
        assertTrue(logMsg.endsWith("D: TEST_TAG Cat List=[Tiger, Eeyore, Abby]"))
    }

}