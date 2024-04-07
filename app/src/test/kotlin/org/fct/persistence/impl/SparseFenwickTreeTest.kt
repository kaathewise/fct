package org.fct.persistence.impl

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.bind
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.intArray
import io.kotest.property.arbitrary.longArray
import io.kotest.property.arbitrary.positiveLong
import io.kotest.property.exhaustive.boolean

class SparseFenwickTreeTest : FreeSpec({
    var tree = SparseFenwickTree()

    "empty tree" - {
        "should have prefix sum 0" {
            tree.prefixSum(100) shouldBe 0
        }

        "should have suffix sum 0" {
            tree.suffixSum(100) shouldBe 0
        }
    }

    val pointsArb: Arb<List<Pair<Long, Int>>> = Arb.bind(
        Arb.longArray(Arb.constant(10), Arb.positiveLong(max = 100)),
        Arb.intArray(Arb.constant(10), Arb.int(min = -100, max = 100))
    ) { longs, ints -> longs.zip(ints.asIterable()).toList() }

    "suffix sum and prefix sum after adding" {
        checkAll(pointsArb, Exhaustive.boolean()) { points, hasDataForQueryPoint ->
            val queryPoint = points[0]
            val dataPoints = if (hasDataForQueryPoint) {
                points
            } else {
                points.filter { it.first != queryPoint.first }
            }
            val expectedSuffixSum =
                dataPoints.filter { it.first >= points[0].first }.sumOf { it.second }
            val expectedPrefixSum =
                dataPoints.filter { it.first <= points[0].first }.sumOf { it.second }

            // checkAll is inside a single test, so context is not re-executed
            tree = SparseFenwickTree()
            dataPoints.forEach { tree.add(it.first, it.second) }

            tree.suffixSum(points[0].first) shouldBe expectedSuffixSum
            tree.prefixSum(points[0].first) shouldBe expectedPrefixSum
        }
    }

    "suffix sum and prefix sum after replacing values" {
        mapOf(1L to 1, 2L to 2, 3L to 3, 4L to 4, 5L to 5)
            .forEach { tree.set(it.key, it.value) }

        tree.set(3L, 10)

        tree.prefixSum(4L) shouldBe 1 + 2 + 10 + 4
        tree.suffixSum(2L) shouldBe 2 + 10 + 4 + 5

        tree.set(3L, 0)

        tree.prefixSum(4L) shouldBe 1 + 2 + 4
        tree.suffixSum(2L) shouldBe 2 + 4 + 5
    }
})