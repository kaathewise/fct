package org.fct

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldContain
import org.fct.persistence.Planes

class ServiceTest : FreeSpec({
    val service = Service(Planes.get(), Parser())

    "returns null on blank or empty line" {
        service.handleLine("  ").shouldBeNull()
        service.handleLine("").shouldBeNull()
    }

    "returns error when cannot parse a line" {
        service.handleLine("dafad 122 asdfad 1122") shouldContain "Cannot parse line"
    }

    "returns null when given an event" {
        service.handleLine("F222 747 DUBLIN LONDON Re-Fuel 2021-03-29T10:00:00 200").shouldBeNull()
    }

    "returns null when given an event deletion" {
        service.handleLine("F222 2021-03-29T10:00:00").shouldBeNull()
    }

    "returns an empty string when there are no events" {
        service.handleLine("2021-03-29T10:00:00").shouldBeEmpty()
    }

    "given basic input" - {
        """F222 747 DUBLIN LONDON Re-Fuel 2021-03-29T10:00:00 200
            |F551 747 PARIS LONDON Re-Fuel 2021-03-29T10:00:00 345
            |F324 313 LONDON NEWYORK Take-Off 2021-03-29T12:00:00 0
            |F123 747 LONDON CAIRO Re-Fuel 2021-03-29T10:00:00 428
            |F123 747 LONDON CAIRO Take-Off 2021-03-29T12:00:00 0
            |F551 747 PARIS LONDON Take-Off 2021-03-29T11:00:00 0
            |F551 747 PARIS LONDON Land 2021-03-29T12:00:00 -120
            |F123 747 LONDON CAIRO Land 2021-03-29T14:00:00 -324
        """.trimMargin().split("\n").forEach(service::handleLine)

        "returns a sorted list of statuses" {
            service.handleLine("2021-03-29T15:00:00") shouldBe ("""
                |F123 Landed 104
                |F222 Awaiting-Takeoff 200
                |F324 In-Flight 0
                |F551 Landed 225""".trimMargin())
        }

        "returns an updated list of statuses after event change" {
            service.handleLine("F551 747 PARIS LONDON Land 2021-03-29T12:00:00 -300")

            service.handleLine("2021-03-29T15:00:00") shouldBe ("""
                |F123 Landed 104
                |F222 Awaiting-Takeoff 200
                |F324 In-Flight 0
                |F551 Landed 45""".trimMargin())
        }

        "returns an updated list of statuses after event removal" {
            service.handleLine("F551 2021-03-29T12:00:00")

            service.handleLine("2021-03-29T15:00:00") shouldBe """
                |F123 Landed 104
                |F222 Awaiting-Takeoff 200
                |F324 In-Flight 0
                |F551 In-Flight 345""".trimMargin()
        }
    }
})