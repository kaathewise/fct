package org.fct

import org.fct.data.Event
import org.fct.data.Status
import org.fct.persistence.Planes
import java.time.Instant

/**
 * Entry point for the application. Processes plain-text input line-by-line and returns plain-text
 * output if needed.
 */
class Service(private val planes: Planes, private val parser: Parser) {
    /**
     * Handles a single line of input.
     *
     * @return a text representation of response for read commands,
     *         null for write commands,
     *         an error string, if unable to parse the command.
     */
    fun handleLine(line: String): String? {
        if (line.isBlank())
            return null
        when (val request = parser.parse(line)) {
            is Event -> planes.insertEvent(request)
            is Event.Key -> planes.deleteEvent(request)
            is Instant -> return planes.getStatus(request).map(Status::toString).sorted()
                .joinToString("\n")
            else -> return "Cannot parse line '$line'."
        }
        return null
    }
}