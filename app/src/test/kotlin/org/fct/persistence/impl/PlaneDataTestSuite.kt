package org.fct.persistence.impl

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.fct.data.Event
import java.time.Instant

class PlaneDataTestSuite : FreeSpec({
    include(planeDataTests("FenwickPlaneData", FenwickPlaneData()))
    include(planeDataTests("TreeMapPlaneData", TreeMapPlaneData()))
})

fun planeDataTests(name: String, instance: PlaneData) = freeSpec {
    "$name should" - {
        "return null event type and fuel when empty" {
            instance.getLastEventTypeAndFuel(Instant.MAX).shouldBeNull()
        }

        "return null event type and fuel when timestamp too low" {
            instance.insertEvent(Instant.ofEpochSecond(100L), Event.Type.LAND, 100)
            instance.getLastEventTypeAndFuel(Instant.MIN).shouldBeNull()
        }

        "aggregate events below given timestamp" {
            instance.insertEvent(Instant.ofEpochSecond(100L), Event.Type.LAND, 100)
            instance.insertEvent(Instant.ofEpochSecond(300L), Event.Type.TAKE_OFF, 300)
            instance.insertEvent(Instant.ofEpochSecond(400L), Event.Type.LAND, 400)
            instance.insertEvent(Instant.ofEpochSecond(200L), Event.Type.RE_FUEL, 200)

            instance.getLastEventTypeAndFuel(Instant.ofEpochSecond(250L)) shouldBe Pair(Event.Type.RE_FUEL, 300)
        }

        "replace events" {
            instance.insertEvent(Instant.ofEpochSecond(200L), Event.Type.LAND, 100)
            instance.insertEvent(Instant.ofEpochSecond(100L), Event.Type.LAND, 100)
            instance.insertEvent(Instant.ofEpochSecond(300L), Event.Type.TAKE_OFF, 300)
            instance.insertEvent(Instant.ofEpochSecond(400L), Event.Type.LAND, 400)
            instance.insertEvent(Instant.ofEpochSecond(200L), Event.Type.RE_FUEL, 200)

            instance.getLastEventTypeAndFuel(Instant.ofEpochSecond(250L)) shouldBe Pair(Event.Type.RE_FUEL, 300)
        }

        "delete events" {
            instance.insertEvent(Instant.ofEpochSecond(100L), Event.Type.LAND, 100)
            instance.insertEvent(Instant.ofEpochSecond(200L), Event.Type.RE_FUEL, 200)
            instance.insertEvent(Instant.ofEpochSecond(300L), Event.Type.TAKE_OFF, 300)
            instance.insertEvent(Instant.ofEpochSecond(400L), Event.Type.LAND, 400)
            instance.deleteEvent(Instant.ofEpochSecond(200L))

            instance.getLastEventTypeAndFuel(Instant.ofEpochSecond(250L)) shouldBe Pair(Event.Type.LAND, 100)
        }
    }
}