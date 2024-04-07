package org.fct.persistence.impl

import java.time.Instant
import java.util.TreeMap
import org.fct.data.Event

/**
 * An implementation of [PlaneData] that uses [SparseFenwickTree] for storing fuel data and
 * a [TreeMap] for storing latest [Event.Type].
 */
class FenwickPlaneData : PlaneData {
    private val eventTypeData = TreeMap<Instant, Event.Type>()
    private val fuelData = SparseFenwickTree()

    override fun insertEvent(timestamp: Instant, eventType: Event.Type, fuelDelta: Int) {
        eventTypeData[timestamp] = eventType
        fuelData.set(timestamp.epochSecond, fuelDelta)
    }

    override fun deleteEvent(timestamp: Instant) {
        eventTypeData.remove(timestamp)
        fuelData.set(timestamp.epochSecond, 0)
    }

    override fun getLastEventTypeAndFuel(timestamp: Instant) : Pair<Event.Type, Int>? {
        val eventType = eventTypeData.floorEntry(timestamp)?.value ?: return null
        return Pair(eventType, fuelData.prefixSum(timestamp.epochSecond))
    }
}