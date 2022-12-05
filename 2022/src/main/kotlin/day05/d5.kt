package day05

import util.getResourceAsFile

fun main(args: Array<String>) {
    var isCrateLines = true
    val crateLines = mutableListOf<String>()
    var supply: Supply? = null
    val rearrangements = mutableListOf<Rearrangement>()

    getResourceAsFile("day05.txt").useLines { lines ->
        lines.forEachIndexed { idx, line ->
            if (isCrateLines) {
                if (line.startsWith(" 1")) {
                    supply = parseSupply(crateLines, line)
                    isCrateLines = false
                } else {
                    crateLines.add(line)
                }
            } else {
                if (line.isNotEmpty()) {
                    rearrangements.add(Rearrangement.from(line))
                }
            }
        }
    }

//    applyRearrangements1(supply!!, rearrangements)
    applyRearrangements2(supply!!, rearrangements)

    supply!!.forEach { stack ->
        print(stack.first().c)
    }
}

fun applyRearrangements1(supply: Supply, rearrangements: List<Rearrangement>) {
    rearrangements.map { rearrange ->
        repeat(rearrange.numberOfCrates) {
            supply[rearrange.toStackIdx].addFirst(
                supply[rearrange.fromStackIdx].removeFirst()
            )
        }
    }
}

// CNSFCGJSM
fun applyRearrangements2(supply: Supply, rearrangements: List<Rearrangement>) {
    rearrangements.map { rearrange ->
        val movingCrates = mutableListOf<Crate>()

        repeat(rearrange.numberOfCrates) {
            movingCrates.add(
                supply[rearrange.fromStackIdx].removeFirst()
            )
        }

        movingCrates.reversed().forEach { crate ->
            supply[rearrange.toStackIdx].addFirst(crate)
        }
    }
}

typealias Supply = List<Stack>
typealias Stack = ArrayDeque<Crate>

fun parseSupply(crateLines: List<String>, positionLine: String): Supply {
    val idxs = positionLine.mapIndexedNotNull { idx: Int, c: Char ->
        if (c.isDigit()) idx
        else null
    }

    val stacks = idxs.mapTo(ArrayList(idxs.size)) { ArrayDeque<Crate>(crateLines.size) } // allocate
    crateLines.forEach { crateLine ->
        idxs.forEachIndexed { pos, idx ->
            Crate.tryFromLineByIndex(crateLine, idx)
                ?.let { stacks[pos].add(it) }
        }
    }
    return stacks
}


@JvmInline
value class Crate(val c: Char) {
    companion object {
        fun tryFromLineByIndex(line: String, idx: Int): Crate? {
            if (idx > line.length) return null
            val c = line.substring(idx, idx + 1).toCharArray()[0]
            return if (c == ' ') null else Crate(c)
        }
    }
}

data class Rearrangement(val numberOfCrates: Int, val fromStackIdx: Int, val toStackIdx: Int) {
    companion object {
        private val pattern = "move (\\d+) from (\\d+) to (\\d+)".toRegex()

        fun from(line: String): Rearrangement {
            val match = pattern.matchEntire(line)!!
            return Rearrangement(
                match.groupValues[1].toInt(),
                match.groupValues[2].toInt() - 1,
                match.groupValues[3].toInt() - 1,
            )
        }
    }
}