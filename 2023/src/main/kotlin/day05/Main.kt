package day05

import util.getResourceAsFile

fun main() {
	val blocks = getResourceAsFile("day05.in").readText().split("\n\n")
	val seeds = blocks[0].removePrefix("seeds: ").split(" ").map { it.toLong() }
	val maps = mutableMapOf<String, Mapping>()

	blocks.drop(1).map { block ->
		val lines = block.split("\n")
		val m = Mapping.from(lines[0])

		lines.drop(1).filter { it.isNotEmpty() }.map { entryLine ->
			m.entries.add(MappingEntry.from(entryLine))
		}
		maps[m.from] = m
	}

	println("seeds: $seeds")
	println("maps:\n" + maps.values.joinToString("\n"))

	val locations = seeds.map { seed ->
		seed to follow(maps, "seed", "location", seed)
	}
	println(locations)
	println(locations.map { it.second }.min())
}

fun follow(maps: MutableMap<String, Mapping>, from: String, terminal: String, n: Long): Long {
//	println("following $from: $n")
	val mapping = maps[from]!!
	val next = mapping.map(n)

	return if (mapping.to == terminal)
		next
	else
		follow(maps, mapping.to, terminal, next)
}

data class Mapping(val from: String, val to: String, val entries: MutableList<MappingEntry>) {

	fun map(source: Long): Long =
		entries.find { it.sourceRange.contains(source) }
			?.let { it.destStart + (source - it.sourceRange.first) }
			?: source

	companion object {

		val headerPattern = "([a-z]+)-to-([a-z]+) map:".toRegex()

		fun from(s: String): Mapping {
			val m = headerPattern.matchEntire(s)!!
			return Mapping(m.groupValues[1], m.groupValues[2], mutableListOf())
		}
	}
}

data class MappingEntry(val sourceRange: LongRange, val destStart: Long) {

	companion object {

		fun from(s: String): MappingEntry {
			val (destRangeStart, sourceRangeStart, rangeLen) = s.split(" ").map { it.toLong() }
			return MappingEntry(
				sourceRange = sourceRangeStart..(sourceRangeStart + rangeLen),
				destStart = destRangeStart
			)
		}
	}
}
