package org.fct.persistence.impl

import java.time.Instant
import org.fct.data.Event

/**
 * Interface for storing and retrieving event data for a single plane.
 */
interface PlaneData {
    /**
     * Inserts an event, replacing it if there already exists an event with the same timestamp.
     *
     * @param timestamp moment in time for which the event is recorded. Only the "seconds"
     *                     precision is supported.
     * @param eventType the type of the event, see [Event.Type].
     * @param fuelDelta the change of the plane's fuel associated with the event.
     */
    fun insertEvent(timestamp: Instant, eventType: Event.Type, fuelDelta: Int)

    /**
     * Deletes an event with a given timestamp if it exists.
     *
     * @param timestamp Only the "seconds" precision is supported.
     */
    fun deleteEvent(timestamp: Instant)

    /**
     * Queries the status of the plane at the end of a given moment in time.
     *
     * @param timestamp moment at which the snapshot is taken. Only the "seconds" precision is supported.
     * @return null, if there are now events up until the [timestamp],
     *         [Pair] of the last [Event.Type] seen and the last known fuel level otherwise.
     */
    fun getLastEventTypeAndFuel(timestamp: Instant): Pair<Event.Type, Int>?
}