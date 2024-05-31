package dunbar.mike.mediabrowser.util

import android.util.Log
import org.jetbrains.annotations.VisibleForTesting
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

interface Logger {

    /**
     * Logs [msg] at debug level, after formatting with/substituting [msgArgs]
     *
     * @see [java.util.Formatter] for syntax
     */
    fun d(tag: String, msg: String, vararg msgArgs: Any?)

    /**
     * Logs [msg] at error level, after formatting with/substituting [msgArgs]
     *
     * @see [java.util.Formatter] for syntax
     */
    fun e(tag: String, msg: String, vararg msgArgs: Any?)
}

enum class Level(val token: String) {
    DEBUG("D"),
    ERROR("E"),
}

object AndroidLogger : Logger {
    override fun d(tag: String, msg: String, vararg msgArgs: Any?) {
        formatMsg(level = Level.DEBUG, tag = tag, msg = msg, msgArgs = msgArgs).let {
            Log.d(tag, it)
        }
    }

    override fun e(tag: String, msg: String, vararg msgArgs: Any?) {
        formatMsg(level = Level.ERROR, tag = tag, msg = msg, msgArgs = msgArgs).let {
            Log.e(tag, it)
        }
    }
}

class ConsoleLogger : Logger {
    @VisibleForTesting
    val logMsgs = mutableListOf<String>()

    override fun d(tag: String, msg: String, vararg msgArgs: Any?) {
        formatMsg(date = Date(), level = Level.DEBUG, tag = tag, msg = msg, msgArgs = msgArgs).let {
            logMsgs.add(it)
            println(it)
        }
    }

    override fun e(tag: String, msg: String, vararg msgArgs: Any?) {
        formatMsg(date = Date(), level = Level.ERROR, tag = tag, msg = msg, msgArgs = msgArgs).let {
            logMsgs.add(it)
            println(it)
        }
    }
}

private fun formatMsg(date: Date? = null, level: Level, tag: String, msg: String, vararg msgArgs: Any?): String {
    var formatStr = "${level.token}: $tag $msg"
    if (date != null) {
        val localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        val dateStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"))

        formatStr = "$dateStr $formatStr"
    }

    return String.format(formatStr, *msgArgs)
}

fun main() {
    ConsoleLogger().d("TEST_TAG", "Name: %s. Age %d.", "Mike", 46)
}