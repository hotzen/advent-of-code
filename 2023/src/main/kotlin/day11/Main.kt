package day11

import util.getResourceAsFile
import kotlin.math.max

data class Pos(val x: Long, val y: Long) {

	override fun toString(): String = "($x/$y)"
}

fun main() {
	var mx = 0L
	var my = 0L

	val gs = getResourceAsFile("day11.in").readText().trim().split("\n")
		.flatMapIndexed { y, line ->
			my = max(my, y.toLong())
			line.mapIndexedNotNull { x, c ->
				mx = max(mx, x.toLong())
				if (c == '#') Pos(x.toLong(), y.toLong())
				else null
			}
		}
//	println("galaxies: $gs")

	val nox = (0..mx).filter { x -> !gs.any { it.x == x } }
	val noy = (0..my).filter { y -> !gs.any { it.y == y } }
//	println("no-galaxies-in-x: $nox\nno-galaxies-in-y: $noy")

	val mult = 1_000_000L
	val exp = gs.map { gpos ->
		val xs = nox.count { gpos.x > it }
		val ys = noy.count { gpos.y > it }
		Pos(
			gpos.x - xs + xs * mult,
			gpos.y - ys + ys * mult
		)
	}
	println("expanded: $exp")

	val pairs = exp.flatMap { g1 -> exp.filter { it != g1 }.map { g2 -> setOf(g1, g2) } }.toSet()
//	println("pairs (${pairs.size}) $pairs")

	val dists = pairs.map { pair -> dist(pair.elementAt(0), pair.elementAt(1)) }
//	println("dists: $dists")

	val sum = dists.sum()
	println("total dists: $sum")
	// 779032247216
}

fun dist(p1: Pos, p2: Pos): Long {
	val dx = if (p1.x < p2.x) p2.x - p1.x else p1.x - p2.x
	val dy = if (p1.y < p2.y) p2.y - p1.y else p1.y - p2.y
	return dx + dy
}
