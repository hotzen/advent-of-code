package day04

import getResourceAsFile

fun main() {
	val pattern = "Card[ ]+([0-9]+): (.+)\\|(.+)".toRegex()

	val cards = getResourceAsFile("day04.in").useLines { lines ->
		lines.map { line ->
			val match = pattern.matchEntire(line)!!
			val id = match.groupValues[1].trim().toInt()
			val card = Card.from(match.groupValues[2], match.groupValues[3])
			id to card
		}.toList()
	}.toMap().toMutableMap()

	val cardIdRange = cards.keys.min()..cards.keys.max()

	cardIdRange.map { cardId ->
		val card = cards[cardId]!!
		val w = card.holdWinners().size
		val wonCardsRange = (cardId + 1)..(cardId + w)
		for (wonCardId in wonCardsRange) {
			cards[wonCardId]?.let {
				it.count += card.count
			}
		}
	}

	println(cards)

	//	val res2 = cards.map {
	//		it.second.worth()
	//	}
	//	println(res2)
	//
	//	val res3 = res2.sum()
	//	println(res3)
	//
	////	val maxReveal = Reveal(red = 12, green = 13, blue = 14)
	////	val filtered = res.filter {(id, reveals) ->
	////		reveals.all { reveal -> reveal.withinMax(maxReveal) }
	////	}
	////	println(filtered)
	////	println(filtered.sumOf { it.first })
	////
	////	val res2 = res.map { it.second }.map { reveals ->
	////		Reveal.max(reveals).power()
	////	}
	////	println(res2.sum())

	println(cards.values.sumOf { it.count })
}

data class Card(val winning: Set<Int>, val holding: Set<Int>, var count: Int) {

	fun holdWinners(): Set<Int> = holding.filter { winning.contains(it) }.toSet()

	fun worth(): Int =
		if (holdWinners().isEmpty()) 0
		else holdWinners().fold(1) { acc, x -> acc * 2 }.div(2)

	companion object {

		fun from(p1: String, p2: String): Card = Card(
			p1.split(" ")
				.filter { it.isNotEmpty() }
				.map { it.toInt() }.toSet(),
			p2.split(" ")
				.filter { it.isNotEmpty() }
				.map { it.toInt() }.toSet(),
			1
		)
	}
}
