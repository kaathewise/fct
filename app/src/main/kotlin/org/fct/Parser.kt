package org.fct

import java.time.Instant
import java.util.regex.Pattern
import org.fct.data.Event
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Parses lines of plain text into application-specific data objects and commands.
 */
class Parser {
    /**
     * Parse a line into a domain object.
     *
     * @return domain object if successful, or null otherwise.
     */
    fun parse(line: String): Any? {
        val fields = line.split(WHITESPACE)

        return listOf(
                ::parseEvent,
                ::parseEventKey,
                ::parseInstant).firstNotNullOfOrNull {
            try {
                it(fields)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun parseEvent(fields: List<String>) =
            Event(
                    key = Event.Key(planeId = fields[0], timestamp = parseInstantField(fields[5])),
                    planeModel = fields[1],
                    origin = fields[2],
                    destination = fields[3],
                    type = Event.Type.entries.first { it.displayName == fields[4] },
                    fuelDelta = fields[6].toInt(),
            )

    private fun parseEventKey(fields: List<String>) =
            Event.Key(
                    planeId = fields[0],
                    timestamp = parseInstantField(fields[1])
            )

    private fun parseInstant(fields: List<String>) = parseInstantField(fields[0])

    private fun parseInstantField(string: String) = Instant.from(FORMATTER.parse(string))

    companion object {
        val FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.of("UTC"))!!
        private val WHITESPACE = Pattern.compile("\\s+")
    }
}