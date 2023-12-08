package day08p2

import util.getResourceAsFile

typealias NodeMap = Map<String, Pair<String, String>>

val nodePattern = "(.{3}) = \\((.{3}), (.{3})\\)".toRegex()

fun main() {
	val input = getResourceAsFile("day08.in").readText().trim().split("\n")
	val moves = input[0]

	val nodesMap = input.drop(2).map { nodePattern.matchEntire(it)!!.groupValues }.map { it[1] to Pair(it[2], it[3]) }.toMap()

	val allSteps = nodesMap.keys.filter { it.endsWith("A") }
		.map { go(it, moves, nodesMap, 1, 0) }
	println(allSteps)

	val common = allSteps.reduce { a,b -> lcm(a,b) }
	println(common)
}

tailrec fun go(fromNode: String, moves: String, map: NodeMap, steps: Long, moveIdx: Int): Long {
	val nextNode =
		if (moves[moveIdx] == 'L') map[fromNode]!!.first
		else map[fromNode]!!.second

	return if (nextNode.endsWith("Z")) steps
	else go(nextNode, moves, map, steps + 1, (moveIdx + 1) % moves.length)
}

fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b
