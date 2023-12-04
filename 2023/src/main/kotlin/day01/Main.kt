package day01

import util.getResourceAsFile

fun main() {
	val s1 = (1..9).toList()
		.map { i -> i.toString() to i }

	val s2 = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
		.mapIndexed { idx, search -> search to idx + 1 }

	val search = s1 + s2
	println(search)


	val res = getResourceAsFile("day01.txt").useLines { lines ->
		lines.map { line ->
			val finds = search.flatMap { (s, d) ->
				line.findAllIndexesOf(s).map { idx -> idx to d }
			}.sortedBy { it.first }

			val num = "" + finds.first().second + finds.last().second
			line to num
		}.toList()
	}
	println(res.joinToString("\n"))
	println(res.map { it.second.toInt() }.sum())
}

fun String.findAllIndexesOf(s: String): Set<Int> =
	(0 until this.length).mapNotNull { start ->
		val idx = this.indexOf(s, start)
		if (idx < 0) null
		else idx
	}.toSet()
