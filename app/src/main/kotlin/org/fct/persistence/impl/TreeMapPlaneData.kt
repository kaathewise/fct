package org.fct.persistence.impl

import java.time.Instant
import java.util.TreeMap
import org.fct.data.Event

/**
 * An implementation of [PlaneData] that uses a single [TreeMap] for storing both fuel data and
 * the latest [Event.Type].
 *
 * It is a highly inefficient implementation, as [getLastEventTypeAndFuel] can take O(N) time,
 * where N is the number of data points, while updates take O(log N) time (which is more than
 * possible constant time).
 */
class TreeMapPlaneData : PlaneData {
    private val data = TreeMap<Instant, Pair<Event.Type, Int>>()

    override fun insertEvent(timestamp: Instant, eventType: Event.Type, fuelDelta: Int) {
        data[timestamp] = Pair(eventType, fuelDelta)
    }

    override fun deleteEvent(timestamp: Instant) {
        data.remove(timestamp)
    }

    override fun getLastEventTypeAndFuel(timestamp: Instant) : Pair<Event.Type, Int>? {
        return data.headMap(timestamp, true).values.reduceOrNull {
            left, right -> Pair(right.first, left.second + right.second)
         }
    }
}