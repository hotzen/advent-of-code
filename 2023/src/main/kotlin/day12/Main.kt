package day12

import util.getResourceAsFile

data class Pos(val x: Long, val y: Long) {

	override fun toString(): String = "($x/$y)"
}

fun main() {
	val spec = getResourceAsFile("day12.ex").readText().trim().split("\n")
		.map { SpringSpec.fromPart2(it) }
//	println("spec: $spec")

	val res = spec.map { springSpec ->
		genSpecs(springSpec.numberOfUnknowns()).count { missingSpec ->
			springSpec.spec(missingSpec.toMutableList()).isValid()
		}
	}
	println(res.sum())
}

fun genSpecs(len: Int): Set<List<Char>> =
	genPermutations(len, "", '.', setOf()) + genPermutations(len, "", '#', setOf())

fun genPermutations(len: Int, s: String, next: Char, res: Set<List<Char>>): Set<List<Char>> {
	if (s.length == len)
		return res + listOf(s.toCharArray().toList())

	return genPermutations(len, s + next, '.', res) + genPermutations(len, s + next, '#', res)
}

val Dots = "\\.+".toRegex()

data class SpringSpec(val conds: String, val groups: List<Int>) {

	fun numberOfUnknowns(): Int = conds.count { it == '?' }

	fun isFullySpecd(): Boolean = !conds.any { it == '?' }

	fun isValid(): Boolean {
		check(isFullySpecd()) { "$this is not even fully specified" }
		val cs = conds.split(Dots).filterNot { it.isEmpty() }
		if (cs.size != groups.size) return false
		return cs.zip(groups).all { (a, b) -> a.length == b }
	}

	fun spec(s: MutableList<Char>): SpringSpec {
		check(s.size == numberOfUnknowns()) { "assignment $s does not match unknowns of $this" }
		val assigned = conds.map { c ->
			if (c == '?') s.removeFirst()
			else c
		}.joinToString("")
		return SpringSpec(assigned, groups)
	}

	companion object {

		fun fromPart1(s: String): SpringSpec {
			val (a, b) = s.split(" ")
			return SpringSpec(a, b.split(",").map(String::toInt))
		}

		fun fromPart2(s: String): SpringSpec {
			val (a, b) = s.split(" ")
			return SpringSpec(
				(Array(5) { a }).joinToString("?"),
				(Array(5) { b }).joinToString(",").split(",").map(String::toInt),
			)
		}
	}
}
