package dunbar.mike.musicbrowser

import dunbar.mike.musicbrowser.util.TestLogger
import org.junit.Test

import org.junit.Assert.*

class LoggerTest {
    private val loggerUnderTest: TestLogger = TestLogger()

    //TODO Parameterize these 2
    @Test
    fun `test debug logging`() {
        // given
        val testTag = "TEST_TAG"
        val logMsgInput = "Name=%s. Age=%d."

        // when
        loggerUnderTest.d(testTag, logMsgInput, "Mike", 46)

        val logMsg = loggerUnderTest.logMsgs[0]

        // then
        val expectedLogMsg = "D: TEST_TAG Name=Mike. Age=46."
        assertEquals(expectedLogMsg, logMsg)
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
        assertEquals("D: TEST_TAG Cat List=[Tiger, Eeyore, Abby]", logMsg)
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
        val expectedLogMsg = "E: TEST_TAG Name: Mike. Age: 46."
        assertEquals(expectedLogMsg, logMsg)
    }


}