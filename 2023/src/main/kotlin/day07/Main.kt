package day07

import getResourceAsFile

typealias CardsFreq = Map<Char, Int>

//val TheOrder = "AKQJT98765432"
val TheOrder = "AKQT98765432J"

fun main() {
	val handBids = getResourceAsFile("day07.in").readText().trim().split("\n")
		.map { Hand.from(it.take(5)) to it.drop(6).toInt() }
		.sortedBy { it.first }
//	println(handBids.joinToString("\n"))

	println(handBids.mapIndexed { idx, (hand, bids) -> bids * (idx + 1) }
				.sum())
}

data class Hand(val cards: String, val freq: CardsFreq, val type: CardsType) : Comparable<Hand> {

	override fun compareTo(other: Hand): Int {
		val cmp1 = other.type.compareTo(this.type) // reversed
		if (cmp1 != 0) return cmp1
		for (idx in cards.indices) {
			val a = TheOrder.indexOf(this.cards[idx])
			val b = TheOrder.indexOf(other.cards[idx])

			val cmp2 = b.compareTo(a) // reversed
			if (cmp2 != 0) return cmp2
		}
		return 0
	}

	companion object {

		fun from(cards: String): Hand {
			val freq = cards.groupingBy { it }.eachCount()
			val jokers = freq['J'] ?: 0
			val jokerlessFreq = freq - 'J'

			return Hand(
				cards,
				freq,
//				CardsType.values().find { it.test(freq) }!!
				CardsType.values().find { it.test(jokerlessFreq, 0..jokers) }!!
			)
		}
	}
}

enum class CardsType(
//	val test: (CardsFreq) -> Boolean,
	val test: (CardsFreq, IntRange) -> Boolean,
) {
	FiveKind(
//		{ it.containsValue(5) },
		// (Hand(cards=6JTJJ, freq={6=1, J=3, T=1}, type=FiveKind), 249)
		{ freq, jokers ->
			5 in jokers || jokers.any { freq.containsValue(5-it) }
		}
	),
	FourKind(
//		{ it.containsValue(4) }
		{ freq, jokers ->
			4 in jokers || jokers.any { freq.containsValue(4-it) }
		}
	),
	FullHouse(
//		{ it.containsValue(3) && it.containsValue(2) }
//		{ freq, jokers -> jokers.any { freq.containsValue(5-it) } }
		{ freq, jokers ->
			(freq.containsValue(3) && freq.containsValue(2))
				|| (1 in jokers && freq.entries.count { it.value == 2 } == 2)
		}
	),
	ThreeKind(
//		{ it.containsValue(3) }
		{ freq, jokers ->
			3 in jokers || jokers.any { freq.containsValue(3-it) }
		}
	),
	TwoPair(
//		{ it.entries.count { it.value == 2 } == 2 }
		{ freq, jokers ->
			freq.entries.count { it.value == 2 } == 2
		}
	),
	OnePair(
		{ freq, jokers ->
			jokers.any { freq.containsValue(2-it) }
		}
	),
	HighCard(
		{ freq, jokers ->
			check(jokers.max() == 0) { "freq:$freq jokers:$jokers"}
			true
		}
	)
}
