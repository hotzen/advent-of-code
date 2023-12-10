package day10

import util.getResourceAsFile

typealias Path = List<Pos>

val Pipes: Map<Char, List<Pos>> = mapOf(
	// connecting north and south.
	'|' to listOf(Pos(0, 1), Pos(0, -1)),

	// connecting east and west.
	'-' to listOf(Pos(-1, 0), Pos(1, 0)),

	// connecting north and east.
	'L' to listOf(Pos(0, -1), Pos(1, 0)),

	// connecting north and west.
	'J' to listOf(Pos(0, -1), Pos(-1, 0)),

	// connecting south and west.
	'7' to listOf(Pos(0, 1), Pos(-1, 0)),

	// connecting south and east.
	'F' to listOf(Pos(0, 1), Pos(1, 0))
)

//val SqueezeDirs = mapOf(
//	// connecting north and south.
//	'|' to listOf(Pos(0, 1), Pos(0, -1)),
//
//	// connecting east and west.
//	'-' to listOf(Pos(-1, 0), Pos(1, 0)),
//
//	// connecting north and east.
//	'L' to listOf(Pos(0, -1), Pos(0, 1), Pos(-1, 0), Pos(1, 0)),
//
//	// connecting north and west.
//	'J' to listOf(Pos(0, -1), Pos(0, 1), Pos(-1, 0), Pos(1, 0)),
//
//	// connecting south and west.
//	'7' to listOf(Pos(0, -1), Pos(0, 1), Pos(-1, 0), Pos(1, 0)),
//
//	// connecting south and east.
//	'F' to listOf(Pos(0, -1), Pos(0, 1), Pos(-1, 0), Pos(1, 0)),
//)


val Dirs = listOf(
	Pos(0, 1), // down
	Pos(0, -1), // up
	Pos(1, 0), // right
	Pos(-1, 0), // left
)

fun main() {
	var start: Pos? = null
	val area = getResourceAsFile("day10.ex3").readText().trim().split("\n")
		.flatMapIndexed { y, line ->
			line.mapIndexed { x, c ->
				Pos(x, y) to c
			}.map { it.also { if (it.second == 'S') start = it.first } }
		}
		.toMap()
	val dim = Pair(
		area.maxOf { it.key.x } + 1,
		area.maxOf { it.key.y } + 1
	)
	println("start: $start")
//	println(area.map { "${it.key} ${it.value}" })

	val loop = Pipes.values.flatMap { dirs ->
		dirs.flatMap { dir ->
			go(start!!, start!! + dir, area, listOf(start!!), emptyList())
		}
	}.maxBy { it.size }
	println(loop.size / 2)

	part2(start!!, area, dim, loop)
}

fun go(prev: Pos, pos: Pos, area: Map<Pos, Char>, path: Path, results: List<Path>): List<Path> {
	if (pos == path.first()) // nice circle
		return results + listOf(path)

	val tile = area[pos]
	if (tile == null || tile == '.') // can't follow
		return results

	if (path.contains(pos)) // partial circle, abort
		return results

	val nexts = Pipes[tile!!]!!.map { it + pos }
		.filter { it != prev } // don't go back
		.filter {
			val nextTile = area[it]
			nextTile == 'S' || Pipes.containsKey(nextTile)
		}

	if (nexts.isEmpty()) // does not lead anywhere
		return results
	else // 1G of stack-memory or boom
		return nexts.flatMap { next -> go(pos, next, area, path + pos, results) }
}

data class Pos(val x: Int, val y: Int) {

	fun orthos(): List<Pos> =
		if (x == 0) listOf(Pos(1, 0), Pos(-1, 0))
		else listOf(Pos(0, 1), Pos(0, -1))

	operator fun plus(other: Pos): Pos = Pos(x + other.x, y + other.y)

	override fun toString(): String = "($x/$y)"
}

fun part2(start: Pos, area: Map<Pos, Char>, dim: Pair<Int, Int>, loop: List<Pos>) {

	val fillArea = area.toMutableMap()
	floodFill(
		Pos(0, 0),
		fillArea,
		dim,
		loop
	)
	draw("flooded including loop", fillArea, dim)

	loop.forEach { fillArea.remove(it) }

	draw("flooded without loop", fillArea, dim)

	val enclosed = fillArea.values.count { it == '.' }
	println("ENCLOSED: $enclosed")
}

fun floodFill(start: Pos, area: MutableMap<Pos, Char>, dim: Pair<Int, Int>, loop: Path) {
	val q = mutableListOf(start)
	while (q.isNotEmpty()) {
		val pos = q.removeFirst()
		q.addAll(
			Dirs.flatMap { dir -> removeNeighbours(pos, dir, area, loop) }
		)
		area.remove(pos)
	}
}

fun removeNeighbours(pos: Pos, dir: Pos, area: MutableMap<Pos, Char>, loop: Path): List<Pos> {
	val rm = mutableListOf<Pos>()

	var prev: Pos = pos
	var next: Pos = pos

	while (true) {
		prev = next
		val prevTile = area[prev]

		next = next + dir

		val nextTile = area[next]
		if (nextTile == null) // out of map
			break

		if (next == Pos(4, 5) && dir == Pos(0, -1)) {
			0 == 1
		}

		// hit the loop
		if (loop.contains(next)) {

			// squeeze through?
			if (nextTile != 'S') {
				// are all orthogonals on the the loop, then we're blocked'

				val orthosOnThePath = dir.orthos().all {
					loop.contains(next + it)
				}

				if (orthosOnThePath) {
					break
				}
////				if (SqueezeDirs[nextTile]!!.contains(dir)) {
//				if (Pipes[nextTile]!!.contains(dir)) {
//					// it's a natural Pipe-direction, we can squeeze along
////					println("squeeze from $prev $prevTile in $dir dir to $next $nextTile")
//				} else {
//					// it's a natural Pipe-direction, w
////					println("NO squeeze from $prev $prevTile in $dir dir to $next $nextTile")
//					break
			} else {
				println("WHATEVER")
			}
		} else {
			area.remove(next)
			rm.add(next)
		}
	}
	return rm
}

fun draw(label: String, area: MutableMap<Pos, Char>, dim: Pair<Int, Int>) {
	println("> $label:")
	for (y in 0 until dim.second) {
		for (x in 0 until dim.first) {
			val p = Pos(x, y)
			print(
				when (val t = area[p]) {
					null -> '▢'
					'|' -> '┃'
					'-' -> '━'
					'L' -> '┗'
					'J' -> '┛'
					'7' -> '┓'
					'F' -> '┏'
					else -> t
				}
			)
		}
		println()
	}
	println()
}
