package day09

import getResourceAsFile

typealias NodeMap = Map<String, Pair<String, String>>

fun main() {
	val hist = getResourceAsFile("day09.in").readText().trim().split("\n")
		.map { it.split(" ").map { it.toInt() } }
	println(hist)

	val extras = hist.map {
//		extrapolate(it)
		extrapolateBackwards(it)
	}
	println(extras)

	println(extras.sum())
}

fun deltas(xs: List<Int>): List<Int> = xs.windowed(2).map { it[1] - it[0] }

fun extrapolate(xs: List<Int>): Int =
	if (xs.all { it == 0 }) {
		0
	} else {
		xs.last() + extrapolate(
			deltas(xs)
		)
	}

fun extrapolateBackwards(xs: List<Int>): Int =
	if (xs.all { it == 0 }) {
		0
	} else {
		xs.first() - extrapolateBackwards(deltas(xs))
	}
