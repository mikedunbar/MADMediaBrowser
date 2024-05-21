package dunbar.mike.mediabrowser.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * @param isoInstant String with format "1966-07-03T00:00:00Z"
 */
fun localDateFromIsoInstant(isoInstant: String): LocalDate {
    val instant: Instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(isoInstant))
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate()
}