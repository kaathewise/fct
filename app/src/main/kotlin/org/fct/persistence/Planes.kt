package org.fct.persistence

import java.time.Instant
import org.fct.data.Event
import org.fct.data.Status
import org.fct.persistence.impl.FenwickPlaneData
import org.fct.persistence.impl.InMemoryPlanesImpl

/**
 * Interface for storing and querying plane data affecting their statuses.
 */
interface Planes {
    /**
     * Store the [event], replacing any other event with the same [Event.key].
     */
    fun insertEvent(event: Event)

    /**
     * Delete the event with a given [eventKey], if it exists.
     */
    fun deleteEvent(eventKey: Event.Key)

    /**
     * Returns an unordered list of planes' [Status]es taken at [timestamp] given. The list contains
     * only planes for which we have at least one data point before or at the given moment.
     */
    fun getStatus(timestamp: Instant): List<Status>

    companion object {
        /**
         * Default implementation of [Planes].
         */
        fun get(): Planes = InMemoryPlanesImpl(::FenwickPlaneData)
    }
}
