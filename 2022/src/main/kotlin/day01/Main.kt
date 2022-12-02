package day01

import util.getResourceAsFile
import java.util.*

const val TOP_N = 3

fun main(args: Array<String>) {
    val ctx = Context(
        PriorityQueue<Elf>(TOP_N+1),
        Elf()
    )
    getResourceAsFile("day01.txt").forEachLine { line(it, ctx) }
    ctx.newElf()

    println(
        (1..TOP_N)
            .sumOf { ctx.q.poll().calories }
    )
}

fun line(l: String, ctx: Context) {
    if (l.isEmpty()) {
        ctx.newElf()
    } else {
        ctx.elf.calories += Integer.valueOf(l)
    }
}

data class Context(val q: PriorityQueue<Elf>, var elf: Elf) {
    fun newElf() {
        q.offer(elf)
        elf = Elf()

        if (q.size > TOP_N) {
            q.poll()
        }
    }
}

data class Elf(var calories: Int = 0) : Comparable<Elf> {
    override fun compareTo(other: Elf): Int = this.calories.compareTo(other.calories)

    override fun toString(): String = Integer.toString(calories)
}
