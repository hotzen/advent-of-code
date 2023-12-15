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

fun part2(init: State) {
	var state = init

	val cycleLen = findCycleLength(state)
	val totalCycles = 1_000_000_000
	val start = (totalCycles - 1) % cycleLen
	println("cycleLen: $cycleLen, should start from $start")

//	for (cycle in 1..1_000_000_000) {
//		state = cycle(state)
//	}
//	val load = calcLoad(state)
//	println("load: $load")
}

//fun main() {
//	val cycleLength = findCycleLength { computeResult(it) }
//	val totalCycles = 1_000_000_000
//	val result = computeResult((totalCycles - 1) % cycleLength)
//	println("Result after $totalCycles cycles: $result")
//}

fun findCycleLength(init: State): Int {

	var tortoise = cycle(init)
	var hare = cycle(cycle(init).first)
	while (tortoise.first != hare.first) {
		tortoise = cycle(tortoise.first)
		hare = cycle(cycle(hare.first).first)
	}

	var mu = 0
	tortoise = cycle(init)
	while (tortoise.first != hare.first) {
		tortoise = cycle(tortoise.first)
		hare = cycle(hare.first)
		mu++
	}

	var lambda = 1
	hare = cycle(tortoise.first)
	while (tortoise.first != hare.first) {
		hare = cycle(hare.first)
		lambda++
	}

	return lambda
}

typealias State = List<String>

fun cycle(state: State): Pair<State, Int> {
	val newState = transpose( // reset
		tiltDown(  // east
			transpose(
				tiltDown(  // south
					transpose(
						tiltUp(  // west
							transpose(
								tiltUp(state) // north
							)
						)
					)
				)
			)
		)
	)
	return newState to calcLoad(newState)
}

fun part1(cols: List<String>) {
	val tiltedCols = tiltUp(cols)
//	println("tiltedCols: $tiltedCols")
	val load = calcLoad(tiltedCols)
	println("load: $load")
}

private fun calcLoad(state: State): Int =
	state.sumOf { col ->
		col.mapIndexed { y, c ->
			if (c == 'O') state[0].length - y
			else 0
		}.sum()
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
