package day15

import util.getResourceAsFile
import java.lang.Math.abs
import kotlin.math.absoluteValue

fun main() {
    val sensorBeacons = getResourceAsFile("day15.txt").useLines { lines ->
        lines.map { line -> SensorWithDetectedBeacon.from(line) }.toList()
    }

//    part1(sensorBeacons)
    part2(sensorBeacons)
}

fun part1(sensorBeacons: List<SensorWithDetectedBeacon>) {
    val bounds = sensorBeacons.map { it.bounds() }.reduce { b1, b2 -> b1.extend(b2) }
    val row = 2000000
    val cols = bounds.min.x..bounds.max.x
    val count = cols.map { Pos(it, row) }
        .filter { pos -> sensorBeacons.any { sens -> sens.withinRangeNotBeacon(pos) } }
        .count()
    println(count)
}

fun part2(sensorBeacons: List<SensorWithDetectedBeacon>) {
    println("determining ranges...")
    val sensorRanges = sensorBeacons
        .map { it.toSensorRanges() }
        .reduce { a, b -> a.extend(b) }
    println("determined ranges")

    val bounds = Bounds(
        min = Pos(0, 0),
        max = Pos(4_000_000, 4_000_000)
    )
    findBeacon(bounds, sensorRanges)
}

fun findBeacon(bounds: Bounds, ranges: SensorRanges) {
    for (x in bounds.min.x..bounds.max.x) {
        val yRanges = ranges[x]
        val yBounds = bounds.min.y..bounds.max.y
        val gap = findGapBetweenRangesWithinBounds(yRanges, yBounds)?.let { Pos(x, it) }

        if (gap != null) {
            val freq = gap.tuningFreq()
            println("GAP $gap with freq $freq")
            return
        }
    }
}

fun findGapBetweenRangesWithinBounds(ranges: List<IntRange>, bounds: IntRange): Int? {
    require(ranges.isNotEmpty()) { "get your bounds straight" }
    var range = ranges.minBy { it.first }
    while (true) {
        val gap = range.last + 1
        if (!bounds.contains(gap)) {
            return null
        }
        val nextRange = ranges.find { it.contains(gap) }
        if (nextRange == null) {
            return gap
        }
        range = nextRange
    }
}

data class SensorWithDetectedBeacon(
    val sensor: Pos,
    val beacon: Pos
) {
    val range = sensor.manhattanDistanceTo(beacon)

    fun withinRange(pos: Pos): Boolean =
        pos.manhattanDistanceTo(sensor) <= range

    fun isBorder(pos: Pos): Boolean =
        pos.manhattanDistanceTo(sensor) == range

    fun withinRangeNotBeacon(pos: Pos): Boolean =
        pos != beacon && withinRange(pos)

    fun bounds() = Bounds(
        Pos(sensor.x - range, sensor.y - range),
        Pos(sensor.x + range, sensor.y + range),
    )

    fun toSensorRanges(): SensorRanges {
        val bounds = bounds()
        val ranges = mutableMapOf<Int, MutableList<IntRange>>()
        for (x in bounds.min.x..bounds.max.x) {
            val dist = abs(sensor.x - x)
            (ranges.getOrPut(x) { mutableListOf() }).add(
                bounds.min.y + dist..bounds.max.y - dist
            )
        }
        return SensorRanges(ranges)
    }

    override fun toString(): String = "SensBeacon($sensor <=$range=> $beacon)"

    companion object {
        val pattern = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()

        fun from(line: String): SensorWithDetectedBeacon {
            val result = pattern.matchEntire(line) ?: throw IllegalArgumentException("invalid '$line'")
            return SensorWithDetectedBeacon(
                Pos(result.groupValues[1].toInt(), result.groupValues[2].toInt()),
                Pos(result.groupValues[3].toInt(), result.groupValues[4].toInt())
            )
        }
    }
}

data class SensorRanges(val ranges: MutableMap<Int, MutableList<IntRange>>) {
    operator fun get(x: Int): List<IntRange> = ranges[x] ?: emptyList()

    fun extend(other: SensorRanges): SensorRanges {
        val newRanges = this.ranges
        for ((x, otherXRanges) in other.ranges) {
            val newXRanges = newRanges[x]
            if (newXRanges == null) {
                newRanges.put(x, otherXRanges)
            } else {
                newXRanges.addAll(otherXRanges)
            }
        }
        return SensorRanges(newRanges)
    }

    override fun toString(): String = "SensorRanges(\n" +
            ranges.entries.sortedBy { it.key }.joinToString("\n") +
            "\n)\n"

}


data class Bounds(val min: Pos, val max: Pos) {
    init {
        require(min.x <= max.x) { "violated ${min.x} <= ${max.x}" }
        require(min.y <= max.y) { "violated ${min.y} <= ${max.y}" }
    }

    fun contains(pos: Pos): Boolean =
        pos.x >= min.x && pos.x <= max.x &&
                pos.y >= min.y && pos.y <= max.y

    fun containsY(y: Int): Boolean =
        y >= min.y && y <= max.y

    fun extend(other: Bounds) = Bounds(
        min.min(other.min),
        max.max(other.max)
    )

    fun totalPositions(): Long =
        (max.x - min.x).toLong() * (max.y - min.y).toLong()

    fun draw(sensors: List<SensorWithDetectedBeacon>): String {
        val sb = StringBuffer()
        val legendLen = 4
        val xLegends: List<List<Char>> = (min.x..max.x).map { "$it".padEnd(legendLen, ' ').toCharArray().toList() }
        val yLegends: List<String> = (min.y..max.y).map { "$it".padEnd(legendLen, ' ') }
        (0 until legendLen).forEach { y ->
            sb.append("".padStart(legendLen, ' '))
            (min.x..max.x).forEachIndexed { x, _ ->
                sb.append(xLegends[x][y])
            }
            sb.append("\n")
        }
        (min.y..max.y).forEachIndexed { yIdx, y ->
            sb.append(yLegends[yIdx])
            (min.x..max.x).forEach { x ->
                val pos = Pos(x, y)
                val sens = sensors.find { it.withinRange(pos) }

                sb.append(
                    when {
                        sens == null -> ' '
                        pos == sens.sensor -> '*'
                        pos == sens.beacon -> 'B'
                        sens.isBorder(pos) -> '█'
                        else -> '▒'
                    }
                )
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    override fun toString(): String = "Bounds($min - $max)"
}

data class Pos(val x: Int, val y: Int) /* : Comparable<Pos> */ {
    fun manhattanDistanceTo(other: Pos): Int =
        (this.x - other.x).absoluteValue + (this.y - other.y).absoluteValue

    fun min(other: Pos): Pos = Pos(
        minOf(this.x, other.x),
        minOf(this.y, other.y)
    )

    fun max(other: Pos): Pos = Pos(
        maxOf(this.x, other.x),
        maxOf(this.y, other.y)
    )

    fun tuningFreq(): Long = (x.toLong() * 4_000_000) + y.toLong()

    override fun toString(): String = "($x/$y)"
}

