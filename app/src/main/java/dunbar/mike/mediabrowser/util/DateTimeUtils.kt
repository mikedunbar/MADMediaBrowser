package dunbar.mike.mediabrowser.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * @param isoInstant String with format "1966-07-03T00:00:00Z"
 *
 * @return LocalDateTime for the [isoInstant]
 */
fun localDateTimeFromIsoInstant(isoInstant: String, zoneOffsetProvider: ZoneOffsetProvider = LocalZoneOffsetProvider): LocalDateTime {
    val instant: Instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(isoInstant))
    return LocalDateTime.ofInstant(instant, zoneOffsetProvider.get())
}

fun formatDateLocalMedium(date: LocalDate): String = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))

interface ZoneOffsetProvider {
    fun get(): ZoneOffset
}

object LocalZoneOffsetProvider : ZoneOffsetProvider {
    override fun get(): ZoneOffset = ZoneOffset.from(OffsetDateTime.now())
}