package day04

import util.getResourceAsFile

fun main(args: Array<String>) {
    val ctx = Context()
    getResourceAsFile("day04.txt").forEachLine { line(it, ctx) }

    println(
//        ctx.pairs.count { it.hasFullContainments() }
        ctx.pairs.count { it.overlapsAtAll() }
    )
}

fun line(l: String, ctx: Context) {
    ctx.pairs.add(
        Pair.from(l)
    )
}

class Context {
    val pairs = mutableListOf<Pair>()

}

data class Pair(
    val section1: IntRange,
    val section2: IntRange
) {
    fun hasFullContainments(): Boolean =
        contains(section1, section2) || contains(section2, section1)

    fun overlapsAtAll(): Boolean =
        section1.intersect(section2).isNotEmpty()

    fun contains(container: IntRange, contained: IntRange): Boolean =
        container.first <= contained.first && container.last >= contained.last

    companion object {
        fun from(line: String): Pair {
            val split = line.split(",").flatMap { it.split("-") }.map { it.toInt() }
            return Pair(
                IntRange(split[0], split[1]),
                IntRange(split[2], split[3]),
            )
        }
    }
}