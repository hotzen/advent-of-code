package day18

import input
import println

fun main() {
	val input = input("day18ex").readLines()
		.map { it.split(" ") }

	part1(input).println()
//	part2(input).println()
}

fun part1(lines: List<List<String>>): Int {
	val trench = mutableListOf<Pos>()

	var p = Pos(0, 0)
	trench.add(p)

	var min = Pos(Integer.MAX_VALUE, Integer.MAX_VALUE)
	var max = Pos(Integer.MIN_VALUE, Integer.MIN_VALUE)

	for (line in lines) {
		val dir = line[0]
		val meters = line[1].toInt()
		for (i in 1..meters) {
			p = p + Dirs[dir]!!
			println("$dir $meters: $p")

			min = min.min(p)
			max = max.max(p)
		}
		trench.add(p)
	}

	var cubic = 0
	for (y in min.y..max.y) {

	}


	return 0
}

fun part2(input: List<String>): Int {
	return input.size
}

data class Pos(val x: Int, val y: Int) {

	fun min(other: Pos) = Pos(
		kotlin.math.min(this.x, other.x),
		kotlin.math.min(this.y, other.y)
	)

	fun max(other: Pos) = Pos(
		kotlin.math.max(this.x, other.x),
		kotlin.math.max(this.y, other.y)
	)

	operator fun plus(other: Pos): Pos = Pos(x + other.x, y + other.y)

	override fun toString(): String = "($x/$y)"
}

val Dirs = mapOf(
	"D" to Pos(0, 1), // down
	"U" to Pos(0, -1), // up
	"R" to Pos(1, 0), // right
	"L" to Pos(-1, 0), // left
)
