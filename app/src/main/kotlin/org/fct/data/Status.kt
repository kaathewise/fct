package org.fct.data

/**
 * Snapshot of a plane's status in a particular moment in time.
 *
 * @property planeId an identifier for the plane, usually its flight number, e.g. "F222".
 * @property flightStatus flight status to be displayed on the departures tableau.
 * @property fuel last known fuel level for the plane.
 */
data class Status(
        val planeId: String,
        val flightStatus: FlightStatus,
        val fuel: Int,
) {
    /**
     * Flight status of a plane.
     *
     * @property displayName human-friendly name used for displaying live departures.
     */
    enum class FlightStatus(val displayName: String) {
        LANDED("Landed"),
        AWAITING_TAKEOFF("Awaiting-Takeoff"),
        IN_FLIGHT("In-Flight");

        override fun toString() = displayName
    }

    override fun toString() = "$planeId $flightStatus $fuel"
}
