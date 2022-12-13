package day13

import util.getResourceAsFile

fun main() {
    val cmp = getResourceAsFile("day13.txt").useLines { lines ->
        lines.chunked(3).mapIndexed { idx, chunk ->
            val pairId = idx + 1
            val line = (idx * 3) + 1

            val c1 = chunk[0]
            val c2 = chunk[1]

            val p1 = Parser.parse(c1)
            val p2 = Parser.parse(c2)
            val smaller = p1.isSmallerThan(p2)
            pairId to smaller
        }.toList()
    }
    println(cmp)
    println(
        cmp.filter { it.second == true }.sumOf { it.first }
    )
}

sealed interface Packet : Comparable<Packet> {

    fun isSmallerThan(other: Packet): Boolean = this <= other

    override fun compareTo(other: Packet): Int =
        when (this) {
            is Ls -> when (other) {
                is Ls -> compareLists(this, other)
                is Num -> this.compareTo(Ls.wrap(other))
            }
            is Num -> when (other) {
                is Ls -> Ls.wrap(this).compareTo(other)
                is Num -> this.n.compareTo(other.n)
            }
        }

    fun compareLists(ls1: Ls, ls2: Ls): Int {
        if (ls1.c.isEmpty() && ls2.c.isNotEmpty()) {
            return -1
        }
        for (i in 0 until ls1.c.size) {
            val itm1 = ls1.c.get(i)
            val itm2 = ls2.c.getOrNull(i)

            if (itm2 == null) {
                println("abort on #$i of\n  $ls1 vs.\n  $ls2 because $itm1 vs. null")
                return 1 // Right side is smaller, so inputs are not in the right order
            }
            val cmp = itm1.compareTo(itm2)
            if (cmp != 0) {
                println("abort on #$i of\n  $ls1 vs.\n  $ls2 because $itm1 vs. $itm2 = $cmp")
                return cmp
            }
        }
        return ls1.c.size.compareTo(ls2.c.size)
    }

    data class Ls(
        val c: MutableList<Packet>
    ) : Packet {
        companion object {
            fun empty() = Ls(mutableListOf())

            fun wrap(num: Num): Ls {
                println("WRAP $num")
                val ls = empty()
                ls.c.add(num)
                return ls
            }
        }
    }

    @JvmInline
    value class Num(
        val n: Int
    ) : Packet
}

object Parser {
    fun parse(s: String): Packet.Ls {
        val stack = mutableListOf<Packet.Ls>()
        var num = ""
        var root: Packet.Ls? = null
        s.forEach { c ->
            when (c) {
                '[' -> {
                    val newLs = Packet.Ls.empty()
                    if (root == null) {
                        root = newLs
                    } else {
                        stack.last().c.add(newLs)
                    }
                    stack.add(newLs)
                }
                ']' -> {
                    if (num.isNotEmpty()) {
                        stack.last().c.add(
                            Packet.Num(num.toInt())
                        )
                        num = ""
                    }
                    stack.removeLast()
                }
                ',' -> {
                    if (num.isNotEmpty()) {
                        stack.last().c.add(
                            Packet.Num(num.toInt())
                        )
                        num = ""
                    }
                }
                else -> {
                    num += c
                }
            }
        }
        return root!!
    }
}