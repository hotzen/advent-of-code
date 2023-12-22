package day02

import getResourceAsFile

fun main() {

	val pattern = "Game ([0-9]+): (.+)".toRegex()

	val res = getResourceAsFile("day02.in").useLines { lines ->
		lines.map { line ->
			val match = pattern.matchEntire(line)!!
			val id = match.groupValues[1].toInt()
			val reveals = match.groupValues[2].split(";").flatMap { it.parseReveals() }
			id to reveals
		}.toList()
	}

//	val maxReveal = Reveal(red = 12, green = 13, blue = 14)
//	val filtered = res.filter {(id, reveals) ->
//		reveals.all { reveal -> reveal.withinMax(maxReveal) }
//	}
//	println(filtered)
//	println(filtered.sumOf { it.first })

	val res2 = res.map { it.second }.map { reveals ->
		Reveal.max(reveals).power()
	}
	println(res2.sum())
}

data class Reveal(val red: Int, val green: Int, val blue: Int) {
	fun withinMax(other: Reveal) =
		this.red <= other.red
			&& this.green <= other.green
			&& this.blue <= other.blue

	fun power(): Int = this.red * this.green * this.blue

	companion object {
		fun max(reveals: List<Reveal>): Reveal =
			Reveal(
				red = reveals.maxOf { it.red },
				green = reveals.maxOf { it.green },
				blue = reveals.maxOf { it.blue },
			)
	}
}

object Patterns {
	val red = """([0-9]+) red""".toRegex()
	val green = """([0-9]+) green""".toRegex()
	val blue = """([0-9]+) blue""".toRegex()
}

private fun String.parseReveals(): List<Reveal> =
	this.split(";").map { revealText ->
		Reveal(
			red = revealText.findAndGiveFirstGroupOrZero(Patterns.red),
			green = revealText.findAndGiveFirstGroupOrZero(Patterns.green),
			blue = revealText.findAndGiveFirstGroupOrZero(Patterns.blue),
		)
	}

fun String.findAndGiveFirstGroupOrZero(r: Regex): Int {
	val res = r.find(this)
	return if (res == null) 0
	else res.groupValues[1].toInt()
}

fun String.findAllIndexesOf(s: String): Set<Int> =
	(0 until this.length).mapNotNull { start ->
		val idx = this.indexOf(s, start)
		if (idx < 0) null
		else idx
	}.toSet()
