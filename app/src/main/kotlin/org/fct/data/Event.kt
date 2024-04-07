package org.fct.data

import java.time.Instant

/**
 * An event from ground crew regarding a change in a plane's status.
 *
 * @property key a unique identifier for the event, see [Event.Key]
 * @property planeModel the model of a plane, e.g. "747" for Boeing-747.
 * @property origin the city of departure, e.g. "DUBLIN".
 * @property destination the city of destination.
 * @property type the event's type, see [Event.Type].
 * @property fuelDelta the change in the plane's fuel associated with this event.
 */
data class Event(
    val key: Key,
    val planeModel: String,
    val origin: String,
    val destination: String,
    val type: Type,
    val fuelDelta: Int,
) {
    /**
     * A unique identifier for an event. There cannot exist two valid events with the same Key.
     *
     * @property planeId an identifier for a plane, usually its flight number, e.g. "F222".
     * @property timestamp the [Instant] at which the event took place.
     */
    data class Key(val planeId: String, val timestamp: Instant)

    /**
     * [Event] type.
     *
     * @property displayName human-friendly name used both for output and for parsing the event log.
     */
    enum class Type(val displayName: String) {
        RE_FUEL("Re-Fuel"),
        TAKE_OFF("Take-Off"),
        LAND("Land")
    }
}
