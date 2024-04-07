package org.fct.persistence.impl

import java.time.Instant
import org.fct.data.Event
import org.fct.data.Status
import org.fct.data.Status.FlightStatus
import org.fct.persistence.Planes

/**
 * An implementation of [Planes] that stores all data in memory. It stores the data for different
 * planes separately and delegates the processing of per-plane data to the implementation of
 * [PlaneData] provided.
 *
 * @property planeDataImpl a [PlaneData] implementation that processes per-plane data.
 */
class InMemoryPlanesImpl(private val planeDataImpl: () -> PlaneData) : Planes {
    private val planeDataByPlane = HashMap<String, PlaneData>()

    override fun insertEvent(event: Event) {
        planeDataByPlane
            .getOrPut(event.key.planeId, planeDataImpl)
            .insertEvent(event.key.timestamp, event.type, event.fuelDelta)
    }

    override fun deleteEvent(eventKey: Event.Key) {
        planeDataByPlane[eventKey.planeId]?.deleteEvent(eventKey.timestamp)
    }

    override fun getStatus(timestamp: Instant): List<Status> {
        return planeDataByPlane
            .mapNotNull { (planeId, data) ->
                data.getLastEventTypeAndFuel(timestamp)?.let { (eventType, fuel) ->
                    Status(
                        planeId = planeId,
                        flightStatus = FLIGHT_STATUS_BY_EVENT_TYPE[eventType]!!,
                        fuel = fuel
                    )
                }
            }
            .toList()
    }

    companion object {
        private val FLIGHT_STATUS_BY_EVENT_TYPE =
            mapOf(
                Event.Type.RE_FUEL to FlightStatus.AWAITING_TAKEOFF,
                Event.Type.TAKE_OFF to FlightStatus.IN_FLIGHT,
                Event.Type.LAND to FlightStatus.LANDED,
            )
    }
}
