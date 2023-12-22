package day19

import input
import println

fun main() {
	val (in_wf, in_parts) = input("day19ex").readText()
		.trim()
		.split("\n\n")
		.map { it.split("\n") }

	val workflows = in_wf.map { Workflow.from(it) }
	workflows.println()

	val parts = in_parts.map { Part.from(it) }
	parts.println()

	part1(workflows, parts).println()
//	part2(input).println()
}

fun part1(workflows: List<Workflow>, parts: List<Part>): Int {

	

	return 0
}

fun part2(input: List<String>): Int {
	return input.size
}

data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {

	companion object {

		val p = "\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)\\}".toRegex()

		fun from(s: String): Part {
			val m = p.matchEntire(s)!!
			return Part(
				m.groupValues[1].toInt(),
				m.groupValues[2].toInt(),
				m.groupValues[3].toInt(),
				m.groupValues[4].toInt(),
			)
		}
	}
}

data class Workflow(val name: String, val rules: List<(Part) -> String?>) {

	fun eval(p: Part): String {
		for (r in rules) {
			val res = r(p)
			if (res != null) return res
		}
		throw IllegalStateException("wtf")
	}

	companion object {

		val p = """([a-z]+)([<>])([0-9]+)\:([a-z]+)""".toRegex()

		fun from(s: String): Workflow {
			val (name, def) = s.split("{")
			val rules = def.removeSuffix("}").split(",")
				.map { rule ->
					p.matchEntire(rule)
						?.let { m ->
							val (m_full, m_cat, m_op, m_num, m_wf) = m.groupValues
							val getter: (Part) -> Int = when (m_cat) {
								"x" -> Part::x
								"m" -> Part::m
								"a" -> Part::a
								"s" -> Part::s
								else -> throw IllegalArgumentException("getter")
							}
							val op: (Int) -> Boolean = when (m_op) {
								"<" -> { x -> x < m_num.toInt() }
								">" -> { x -> x > m_num.toInt() }
								else -> throw IllegalArgumentException("op")
							}
							val rule: (Part) -> String? = { part ->
								if (op(getter(part))) m_wf
								else null
							}
							rule
						}
						?: { part -> rule }
				}
			return Workflow(name, rules)
		}
	}
}
