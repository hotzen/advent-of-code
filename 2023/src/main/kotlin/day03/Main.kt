package day03

import util.getResourceAsFile

fun main() {

	val symbols = mutableSetOf<Sym>()
	val numbers = mutableMapOf<Int, Set<Num>>()

	getResourceAsFile("day03.in").useLines { lines ->
		lines.forEachIndexed { y, line ->
			val (lineSymbols, lineNumbers) = line.extractNumbersAndSymbols(y)
			symbols.addAll(lineSymbols)

			if (lineNumbers.isNotEmpty()) {
				numbers[y] = lineNumbers
			}
		}
	}
	println("symbols ${symbols.size}")
	println("numbers ${numbers.entries.sumOf { it.value.size }}")

//	val numbersNearSymbols = symbols.flatMap { it.pos.surroundingPositions() }.mapNotNull { pos ->
//		numbers[pos.y]?.find { it.xs.contains(pos.x) }
//	}.toSet()
//	println(numbersNearSymbols.sumOf { it.n })

	val gearRatios = symbols.filter { it.s == '*' }.mapNotNull { gear ->
		val gearNums = gear.pos.surroundingPositions().mapNotNull { pos ->
			numbers[pos.y]?.find { it.xs.contains(pos.x) }
		}.toSet()
//		println("gear? $gear (${gearNums.size}) ${gearNums.map{it.n}}")
		if (gearNums.size == 2) gearNums.fold(1) { acc, num -> acc * num.n }
		else null
	}
	println(gearRatios.sum())
}

fun String.extractNumbersAndSymbols(y: Int): Pair<Set<Sym>, Set<Num>> {
	val symbols = mutableSetOf<Sym>()
	val numbers = mutableSetOf<Num>()

	var currentNumStartX = -1
	val currentNumChars = mutableListOf<Char>()

	this.forEachIndexed { x, c ->
		if (c.isDigit()) {
			currentNumChars.add(c)
			if (currentNumStartX == -1) {
				currentNumStartX = x
			}
		} else {
			if (currentNumStartX != -1) {
				numbers.add(Num(currentNumChars.joinToInt(), currentNumStartX until x, y))
				currentNumStartX = -1
				currentNumChars.clear()
			}
			if (c != '.') {
				symbols.add(Sym(c, Pos(x, y)))
			}
		}
	}
	if (currentNumStartX != -1) {
		numbers.add(Num(currentNumChars.joinToInt(), currentNumStartX until this.length, y))
	}
	return symbols.toSet() to numbers.toSet()
}

fun MutableList<Char>.joinToInt(): Int = this.joinToString("").toInt()

fun allNumberPos(number: String, pos: Pos): List<Pos> =
	(0 until number.length).map { x -> pos.plusX(x) }

data class Pos(val x: Int, val y: Int) {

	fun plusX(i: Int) = Pos(x + i, y)

	fun surroundingPositions(): Set<Pos> = buildSet {
		add(Pos(x + 1, y))
		add(Pos(x - 1, y))
		add(Pos(x, y + 1))
		add(Pos(x, y - 1))
		add(Pos(x + 1, y + 1))
		add(Pos(x + 1, y - 1))
		add(Pos(x - 1, y + 1))
		add(Pos(x - 1, y - 1))
	}
}

data class Num(val n: Int, val xs: IntRange, val y: Int)

data class Sym(val s: Char, val pos: Pos)
