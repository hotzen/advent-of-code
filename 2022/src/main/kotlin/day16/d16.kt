package day16

import util.getResourceAsFile
import java.lang.Integer.max

fun main() {
    val valves = getResourceAsFile("day16.txt").useLines { lines ->
        lines.map { Valve.from(it) }.map { it.id to it }.toMap()
    }
    println(valves)

    part1(valves)
}

fun part1(valves: Map<String, Valve>) {
    val res = go(valves, valves.getValve("AA"), emptySet(), 30, mutableMapOf())
    println("max flow: $res")
}

data class CacheKey(
    val v: Valve,
    val opened: Set<Valve>,
    val minutesLeft: Int
) {
    override fun toString(): String =
        "CacheKey(${v.id} / open: ${opened.map { it.id }.joinToString(",")} @ $minutesLeft)"
}

fun go(
    valves: Map<String, Valve>,
    v: Valve,
    opened: Set<Valve>,
    minutesLeft: Int,
    cache: MutableMap<CacheKey, Int>
): Int {
    val cacheKey = CacheKey(v, opened, minutesLeft)

    if (minutesLeft <= 0) {
        return 0
    }

    val cachedFlow = cache[cacheKey]
    if (cachedFlow != null) {
        return cachedFlow
    }

    var bestFlow = 0
    val openedFlow =
        if (!opened.contains(v))
            v.calcFlow(minutesLeft - 1)
        else 0

    for (nextValveId in v.tunnelsTo) {
        val nextValve = valves.getValve(nextValveId)

        if (openedFlow > 0) {
            bestFlow = max(
                bestFlow,
                openedFlow + go(valves, nextValve, opened + v, minutesLeft - 2, cache)
            )
        }
        bestFlow = max(
            bestFlow,
            go(valves, nextValve, opened, minutesLeft - 1, cache)
        )
    }

    cache.put(cacheKey, bestFlow)
    return bestFlow
}

fun Map<String, Valve>.getValve(id: String): Valve =
    this.get(id) ?: throw IllegalArgumentException("no valve $id")

data class Valve(val id: String, val flowRate: Int, val tunnelsTo: Set<String>) {

    fun calcFlow(minutesLeft: Int) = flowRate * minutesLeft

    companion object {
        val pattern = "Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)".toRegex()

        fun from(s: String): Valve {
            val match = pattern.matchEntire(s) ?: throw IllegalArgumentException("invalid '$s'")
            return Valve(
                match.groupValues[1],
                match.groupValues[2].toInt(),
                match.groupValues[3].split(", ").toSet(),
            )
        }
    }
}
