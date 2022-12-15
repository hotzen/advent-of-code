package day15

import util.getResourceAsFile
import kotlin.math.absoluteValue

fun main() {
    val sensorBeacons = getResourceAsFile("day15.txt").useLines { lines ->
        lines.map { line -> SensorWithDetectedBeacon.from(line) }.toList()
    }

    println(sensorBeacons.joinToString("\n"))
    val bounds = sensorBeacons.map { it.bounds() }.reduce { b1, b2 -> b1.extend(b2) }
    println(bounds)

    val row = 2000000
    val cols = bounds.min.x..bounds.max.x
    val count = cols.map { Pos(it, row) }
        .filter { pos -> sensorBeacons.any { sens -> sens.withinRangeNotBeacon(pos) } }
        .count()
    println(count)
}

data class SensorWithDetectedBeacon(
    val sensor: Pos,
    val beacon: Pos
) {
    val range = sensor.manhattanDistanceTo(beacon)

    fun withinRange(pos: Pos): Boolean =
        pos.manhattanDistanceTo(sensor) <= range

    fun withinRangeNotBeacon(pos: Pos): Boolean =
        pos != beacon && withinRange(pos)

    fun bounds() = Bounds(
        Pos(sensor.x - range, sensor.y - range),
        Pos(sensor.x + range, sensor.y + range),
    )

    companion object {
        val pattern = "Sensor at x=(\\-?\\d+), y=(\\-?\\d+): closest beacon is at x=(\\-?\\d+), y=(\\-?\\d+)".toRegex()

        fun from(line: String): SensorWithDetectedBeacon {
            val result = pattern.matchEntire(line) ?: throw IllegalArgumentException("invalid '$line'")
            return SensorWithDetectedBeacon(
                Pos(result.groupValues[1].toInt(), result.groupValues[2].toInt()),
                Pos(result.groupValues[3].toInt(), result.groupValues[4].toInt())
            )
        }
    }
}

data class Bounds(val min: Pos, val max: Pos) {
    fun contains(pos: Pos): Boolean =
        pos.x >= min.x && pos.x <= max.x &&
                pos.y >= min.y && pos.y <= max.y

    fun extend(other: Bounds) = Bounds(
        min.min(other.min),
        max.max(other.max)
    )

    override fun toString(): String = "Bounds($min - $max)"
}


data class Pos(val x: Int, val y: Int) /* : Comparable<Pos> */ {
    operator fun plus(move: Pos): Pos = Pos(
        this.x + move.x,
        this.y + move.y
    )

    fun manhattanDistanceTo(other: Pos): Int =
        (this.x - other.x).absoluteValue + (this.y - other.y).absoluteValue

//    fun delta(other: Pos): Pos = Pos(
//        other.x - this.x,
//        other.y - this.y
//    )
//
//    fun clampToSignedOne() = Pos(
//        x.coerceIn(-1, 1),
//        y.coerceIn(-1, 1)
//    )

    fun min(other: Pos): Pos = Pos(
        minOf(this.x, other.x),
        minOf(this.y, other.y)
    )

    fun max(other: Pos): Pos = Pos(
        maxOf(this.x, other.x),
        maxOf(this.y, other.y)
    )

//    override fun compareTo(other: Pos): Int {
//        val delta = delta(other).clampToSignedOne()
//        if (delta.x == -1 || delta.y == -1) return -1
//        if (delta.x == 1 || delta.y == 1) return 1
//        println("Pos.compareTo REALLY 0? $delta")
//        return 0
//    }

    override fun toString(): String = "($x/$y)"
}
