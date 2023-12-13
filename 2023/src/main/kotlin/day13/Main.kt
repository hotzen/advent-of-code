package day13

import util.getResourceAsFile
import kotlin.math.max

data class Pos(val x: Int, val y: Int) {

	override fun toString(): String = "($x/$y)"
}

fun main() {
	val patterns = getResourceAsFile("day13.in").readText().trim().split("\n\n")
		.map { Pattern.from(it) }

	println(patterns.map { it.scanPart1() }.sum())
}

data class Pattern(val max: Pos, val rows: List<List<Int>>, val cols: List<List<Int>>) {

	fun scanPart1(): Int {
		val colScans = (0 until max.x).mapNotNull { x ->
			val start = Pair(x, x + 1)
			findReflectionStartingAt({ x -> cols[x] }, max.x, start, 0)
				?.let { depth -> if (depth > 0) x to depth else null }
		}
		val rowScans = (0 until max.y).mapNotNull { y ->
			val start = Pair(y, y + 1)
			findReflectionStartingAt({ y -> rows[y] }, max.y, start, 0)
				?.let { depth -> if (depth > 0) y to depth else null }
		}
		check(colScans.size <= 1) { "many colScans: $colScans" }
		check(rowScans.size <= 1) { "many rowScans: $rowScans" }
		println("colScans: $colScans / rowScans: $rowScans")
		return colScans.map { it.first + 1 }.sum() +
			(rowScans.map { it.first + 1 }.sum() * 100)
	}

	fun findReflectionStartingAt(f: (Int) -> List<Int>, max: Int, start: Pair<Int, Int>, depth: Int): Int? {
		val a = start.first - depth
		val b = start.second + depth

		if (a < 0 || a > max) return depth
		if (b < 0 || b > max) return depth

		// this must not happen for a perfect reflection
		if (f(a) != f(b)) return null

		return findReflectionStartingAt(f, max, start, depth + 1)
	}

	companion object {

		fun from(s: String): Pattern {
			var mx = 0
			var my = 0
			val rows = s.trim().split("\n").mapIndexed { y, line ->
				my = max(my, y)
				line.mapIndexedNotNull() { x, c ->
					mx = max(mx, x)
					if (c == '#') x
					else null
				}
			}
			val cols = (0..mx).map { x ->
				(0..my).mapNotNull { y ->
					rows[y].find { it == x }?.let { y }
				}
			}
			return Pattern(Pos(mx, my), rows, cols)
		}
	}

	override fun toString(): String = "Pattern(max: $max\n  rows: $rows\n  cols: $cols\n)"
}
