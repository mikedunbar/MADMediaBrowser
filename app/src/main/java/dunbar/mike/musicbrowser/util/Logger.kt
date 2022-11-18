package dunbar.mike.musicbrowser.util

import android.util.Log

interface Logger {

    fun d(tag: String, msg: String, vararg msgArgs: Any?)

    fun e(tag: String, msg: String, vararg msgArgs: Any?)

    companion object {
        const val DEBUG_TAG = "RED"
    }

}

enum class Level(val token: String) {
    DEBUG("D"),
    ERROR("E"),
}

object AndroidLogger : Logger {
    override fun d(tag: String, msg: String, vararg msgArgs: Any?) {
        formatMsg(Level.DEBUG, tag, msg, *msgArgs).let {
            Log.d(tag, it)
        }
    }

    override fun e(tag: String, msg: String, vararg msgArgs: Any?) {
        formatMsg(Level.ERROR, tag, msg, *msgArgs).let {
            Log.e(tag, it)
        }
    }
}

class TestLogger : Logger {
    val logMsgs = mutableListOf<String>()

    override fun d(tag: String, msg: String, vararg msgArgs: Any?) {
        formatMsg(Level.DEBUG, tag, msg, *msgArgs).let {
            logMsgs.add(it)
            println(it)
        }
    }

    override fun e(tag: String, msg: String, vararg msgArgs: Any?) {
        formatMsg(Level.ERROR, tag, msg, *msgArgs).let {
            logMsgs.add(it)
            println(it)
        }
    }
}

private fun formatMsg(level: Level, tag: String, msg: String, vararg msgArgs: Any?): String {
    val formatStr = "${level.token}: $tag $msg"

    return String.format(formatStr, *msgArgs)
}

fun main() {
    TestLogger().d("TEST_TAG", "Name: %s. Age %d.", "Mike", 46)
}