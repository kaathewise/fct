package org.fct

import java.time.Instant
import org.fct.data.Event
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum
import java.time.LocalDateTime
import java.time.ZoneId

class ParserTest : FreeSpec({
    val parser = Parser()

    fun formatInstant(timestamp: Instant) =
        Parser.FORMATTER.format(LocalDateTime.ofInstant(timestamp, ZoneId.of("UTC")))

    val timestampArb =
        Arb.instant(minValue = Instant.EPOCH, maxValue = Instant.ofEpochSecond(171251465000L))

    "parses Event" {
        checkAll(
            Exhaustive.enum<Event.Type>(),
            timestampArb,
            Arb.int()
        ) { eventType, timestamp, fuelDelta ->
            val line =
                "F551   747 PARIS   LONDON ${eventType.displayName}\t${formatInstant(timestamp)} \t$fuelDelta"
            parser.parse(line) shouldBe
                    Event(
                        Event.Key(
                            planeId = "F551",
                            timestamp = timestamp
                        ),
                        planeModel = "747",
                        origin = "PARIS",
                        destination = "LONDON",
                        type = eventType,
                        fuelDelta = fuelDelta
                    )
        }
    }

    "parses Event.Key" {
        checkAll(
            timestampArb
        ) { timestamp ->
            parser.parse("F551 \t${formatInstant(timestamp)}") shouldBe
                    Event.Key(planeId = "F551", timestamp = timestamp)
        }
    }

    "parses Instant" {
        checkAll(
            timestampArb
        ) { timestamp ->
            parser.parse("${formatInstant(timestamp)}\n") shouldBe timestamp
        }
    }

    "returns null when unable to parse" {
        parser.parse("kjlk12j31231$ sd 2312 ss").shouldBeNull()
    }
})