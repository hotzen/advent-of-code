package day08

import getResourceAsFile

typealias NodeMap = Map<String, Pair<String, String>>

fun main() {
	val input = getResourceAsFile("day08.in").readText().trim().split("\n")
	val moves = input[0]

	val nodePattern = "([A-Z]{3}) = \\(([A-Z]{3}), ([A-Z]{3})\\)".toRegex()
	val nodesMap = input.drop(2)
		.map { nodePattern.matchEntire(it)!!.groupValues }.map { it[1] to Pair(it[2], it[3]) }
		.toMap()
//	println("nodesMap: $nodesMap")

	val numMoves = go("AAA", "ZZZ", moves, nodesMap, 1, 0)
	println(numMoves)
}

tailrec fun go(fromNode: String, terminal: String, moves: String, map: NodeMap, steps: Long, moveIdx: Int): Long {
	val nextNode =
		if (moves[moveIdx] == 'L') map[fromNode]!!.first
		else map[fromNode]!!.second

	return if (nextNode == terminal) steps
	else go(nextNode, terminal, moves, map, steps + 1, (moveIdx + 1) % moves.length)
}
