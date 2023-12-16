package day16

import util.getResourceAsFile
import java.util.LinkedList
import java.util.Queue
import kotlin.math.max

fun main() {
	var mx = 0
	var my = 0
	val tiles = getResourceAsFile("day16.ex").readText().trim().split("\n")
		.flatMapIndexed { y: Int, line ->
			my = max(my, y)
			line.mapIndexed { x, c ->
				mx = max(mx, x)
				Pos(x, y) to Tile(c)
			}
		}.toMap()
	val max = Pos(mx, my)

	println(
		go(tiles, Pos(0, 0) to Dir.RIGHTWARDS)
	)
//	 6795 at 742000

//	val starts = listOf(
//		(0..my).map { y -> Pos(0, y) to Dir.RIGHTWARDS },
//		(0..mx).map { x -> Pos(x, 0) to Dir.DOWNWARDS },
//		(0..mx).map { x -> Pos(x, max.y) to Dir.UPWARDS },
//		(0..my).map { y -> Pos(max.x, y) to Dir.LEFTWARDS }
//	).flatten()
//
//	val results = runBlocking(Dispatchers.Default) {
//		starts.pmap { start -> start to go(resetTiles(tiles), start) }
//	}
//	val max1 = results.maxOf { it.second }
//	println("MAX1: $max1")

//	println("\n\nphase 2")
//
//	val starts2 = starts.filter { start ->
//		results.any { (resStart, (res, resAbort)) -> res.first == start && res.second = true }
//	}
//
//	val results2 = runBlocking(Dispatchers.Default) {
//		st.pmap { start -> go(resetTiles(tiles), start, 10) }
//	}

	// 11501 too high
	// 7125 too low
	// 7139 too low
	// 7143 not right
}

fun resetTiles(tiles: Map<Pos, Tile>): Map<Pos, Tile> =
	tiles.map { (pos, tile) ->
		pos to Tile(tile.c, energized = false)
	}.toMap()

fun go(tiles: Map<Pos, Tile>, start: Pair<Pos, Dir>): Int {
	println("STARTING SEARCH FROM $start")

	val q = ArrayDeque<Beam>()
	q.add(Beam(start, mutableListOf()))

	while (q.isNotEmpty()) {
		val beam = q.removeFirst()
		tiles[beam.now.first]?.let { tile ->
			if (!tile.energized) {
				tile.energized = true
			}
			q.addAll(
				beam.go(tile.c)
			)
		}
	}
	return tiles.count { it.value.energized }
}

//fun go(tiles: Map<Pos, Tile>, start: Pair<Pos, Dir>, abortMulti: Int): Pair<Int, Boolean> {
//	println("STARTING SEARCH FROM $start")
//
//	val q = mutableListOf<Pair<Pos, Dir>>(start)
//
//	val maxAwaitChanges = tiles.size * abortMulti
//	var countSinceLastChange = 0
//	var totalEnergized = 0
//
//	while (q.isNotEmpty()) {
//		val (pos, dir) = q.removeFirst()
//		tiles[pos]?.let { tile ->
//			if (tile.energized) {
//				countSinceLastChange++
//			} else {
//				tile.energized = true
//				countSinceLastChange = 0
//				totalEnergized = tiles.count { it.value.energized }
//			}
//			q.addAll(
//				nextDirs(tile.c, dir)
//					.map { nextDir -> pos + nextDir to nextDir }
//					.filter { it != start } // is this too naive?
//			)
//		}
//
//		if (countSinceLastChange > maxAwaitChanges) {
//			println("ABORTED SEARCH AFTER $countSinceLastChange FROM $start with $totalEnergized")
//			return totalEnergized to true
//		}
//	}
//
//	println("FINISHED SEARCH FROM $start with $totalEnergized")
//	return totalEnergized to false
//}

data class Beam(val now: Pair<Pos, Dir>, val vis: List<Pair<Pos, Dir>>) {
	fun go(c: Char): List<Beam> =
		nextDirs(c, now.second).mapNotNull { nextDir -> tryNext(nextDir) }

	fun tryNext(nextDir: Dir): Beam? {
		val next = now.first + nextDir to nextDir
//		return if (vis.any { it == next })
		return if (vis.any { it.first == next.first })
			null
		else
			Beam(next, vis + listOf(now))
	}
}

//fun printEnergized(tiles: Map<Pos, Tile>) {
//	println()
//	for (y in 0 until 10) {
//		for (x in 0 until 10) {
//			print(
//				if (tiles[Pos(x, y)]!!.energized) '#' else '.'
//			)
//		}
//		println()
//	}
//}

fun nextDirs(c: Char, dir: Dir) = when (c) {
	'.' -> listOf(dir)
	'|' -> when (dir) {
		Dir.RIGHTWARDS, Dir.LEFTWARDS -> listOf(Dir.UPWARDS, Dir.DOWNWARDS)
		Dir.UPWARDS, Dir.DOWNWARDS -> listOf(dir)
	}
	'-' -> when (dir) {
		Dir.RIGHTWARDS, Dir.LEFTWARDS -> listOf(dir)
		Dir.UPWARDS, Dir.DOWNWARDS -> listOf(Dir.LEFTWARDS, Dir.RIGHTWARDS)
	}
	'\\' -> when (dir) {
		Dir.RIGHTWARDS -> listOf(Dir.DOWNWARDS)
		Dir.LEFTWARDS -> listOf(Dir.UPWARDS)
		Dir.UPWARDS -> listOf(Dir.LEFTWARDS)
		Dir.DOWNWARDS -> listOf(Dir.RIGHTWARDS)
	}
	'/' -> when (dir) {
		Dir.RIGHTWARDS -> listOf(Dir.UPWARDS)
		Dir.LEFTWARDS -> listOf(Dir.DOWNWARDS)
		Dir.UPWARDS -> listOf(Dir.RIGHTWARDS)
		Dir.DOWNWARDS -> listOf(Dir.LEFTWARDS)
	}
	else -> throw IllegalArgumentException("$c")
}

enum class Dir(val x: Int, val y: Int) {
	RIGHTWARDS(1, 0),
	LEFTWARDS(-1, 0),
	UPWARDS(0, -1),
	DOWNWARDS(0, 1)
}

data class Pos(val x: Int, val y: Int) {

	operator fun plus(dir: Dir) = Pos(
		this.x + dir.x,
		this.y + dir.y
	)
}

data class Tile(val c: Char, var energized: Boolean = false)
