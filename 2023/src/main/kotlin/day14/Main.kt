package day14

import util.getResourceAsFile

fun main() {
	val lines = getResourceAsFile("day14.ex").readText().trim().split("\n").toMutableList()
	val colsArr = lines.removeFirst().map { c -> "$c" }.toTypedArray()
	lines.map { line ->
		line.mapIndexed { x, c ->
			colsArr[x] = colsArr[x] + c
		}
	}
	val cols = colsArr.toList()

//	part1(cols)
	part2(cols)
}

fun part2(cols: List<String>) {
	var foo = cols

	for (cycle in 1..1_000_000_000) {
		foo = cycle(foo)
		if (cycle % 10000 == 0) {
			println(cycle)
//			println("cycle #$cycle:\n" + transpose(foo).joinToString("\n") + "\n")
		}
	}
	val load = calcLoad(foo)
	println("load: $load")
}

fun cycle(foo: List<String>): List<String> =
	transpose( // reset
		tiltDown(  // east
			transpose(
				tiltDown(  // south
					transpose(
						tiltUp(  // west
							transpose(
								tiltUp(foo) // north
							)
						)
					)
				)
			)
		)
	)

fun part1(cols: List<String>) {
	val tiltedCols = tiltUp(cols)
//	println("tiltedCols: $tiltedCols")
	val load = calcLoad(tiltedCols)
	println("load: $load")
}

private fun calcLoad(tiltedCols: List<String>): Int {
	val loadedCols = tiltedCols.map { col ->
		col.mapIndexed { y, c ->
			if (c == 'O') tiltedCols[0].length - y
			else 0
		}.sum()
	}
	return loadedCols.sum()
}

fun tiltUp(ss: List<String>): List<String> = ss.map { s ->
	s.split("#").joinToString("#") { tilt(it) }
}

fun tiltDown(ss: List<String>): List<String> = tiltUp(ss.map { it.reversed() }).map { it.reversed() }

fun tilt(s: String): String = s.replace(".", "").padEnd(s.length, '.')

fun transpose(ss: List<String>): List<String> {
	val len = ss[0].length
	return (0 until len).map { a ->
		ss.map { it[a] }.joinToString("")
	}
}
