package day03

import util.getResourceAsFile

fun main(args: Array<String>) {
    val ctx = Context()
    getResourceAsFile("day03.txt").forEachLine { line(it, ctx) }

    println(
//        ctx.rucksacks.sumOf { it.bothErrorsPriority() } part 1
        ctx.groups.sumOf { it.badge().priority() }
    )
}

fun line(l: String, ctx: Context) {
    val g = if (ctx.groups.last().isFull()) ctx.newGroup()
    else ctx.groups.last()

    val half = l.length.div(2)
    val rucksack = g.newRucksack()

    l.toCharArray().forEachIndexed { i, c ->
        if (i < half) rucksack.putLeft(ItemType(c))
        else rucksack.putRight(ItemType(c))
    }
}

class Context {
    val groups = mutableListOf<Group>()

    fun newGroup(): Group {
        val g = Group()
        groups.add(g)
        return g
    }

    init {
        newGroup()
    }
}

class Group {
    val rucksacks = ArrayList<RucksackCompartments>(MAX_SIZE)

    fun newRucksack(): RucksackCompartments {
        val r = RucksackCompartments()
        rucksacks.add(r)
        return r
    }

    fun isFull(): Boolean = rucksacks.size == MAX_SIZE

    fun badge(): ItemType =
        rucksacks[0].all
            .intersect(rucksacks[1].all)
            .intersect(rucksacks[2].all)
            .single()

    companion object {
        const val MAX_SIZE = 3
    }
}

class RucksackCompartments {
    val left = mutableSetOf<ItemType>() // part 1
    val right = mutableSetOf<ItemType>() // part 1
    val both = mutableSetOf<ItemType>() // part 1
    val all = mutableSetOf<ItemType>()

    fun putLeft(i: ItemType) {
        if (right.contains(i)) both.add(i)
        left.add(i)
        all.add(i)
    }

    fun putRight(i: ItemType) {
        if (left.contains(i)) both.add(i)
        right.add(i)
        all.add(i)
    }

    // part 1
    fun bothErrorsPriority(): Int =
        both.sumOf { it.priority() }
}

@JvmInline
value class ItemType(val symbol: Char) {
    // ASCII A-Z: 65-90 => 27 through 52
    // ASCII a-z: 97-122 => 1 through 26
    fun priority() = when (val v = symbol.code) {
        in 65..90 -> v - 38
        in 97..122 -> v - 96
        else -> throw IllegalArgumentException("illegal symbol $symbol with value $v")
    }
}