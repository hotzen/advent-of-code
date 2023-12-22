package day06

import getResourceAsFile

fun main() {
//	val (times, dists) = getResourceAsFile("day06.ex").readText().trim()
//		.split("\n")
//		.map { it.trim().drop(11).split(" ").filter { it.isNotEmpty() }.map { it.toInt() }}
	val (times, dists) = getResourceAsFile("day06.in").readText().trim()
		.split("\n")
		.map { it.trim().drop(11).replace("\\s+".toRegex(), "").toLong() }
		.map { listOf(it) }

	println("time: $times")
	println("dist: $dists")

	val res = times.mapIndexed { idx, t ->
		val recordDist = dists[idx]
		val holdRange = 1 until t

		holdRange.mapNotNull { holdTime ->
			val remTime = t - holdTime
			val speed = holdTime
			val dist = remTime * speed

			if (dist > recordDist) holdTime to dist
			else null
		}
	}
//	println(res.joinToString("\n\n"))
	println(res.map { it.size }.fold(1) { acc, i -> acc * i } )
}
