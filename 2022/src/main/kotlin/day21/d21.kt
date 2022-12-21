package day21

import util.getResourceAsFile

fun main() {
    val monkeys = getResourceAsFile("day21.txt").useLines { lines ->
        lines.map { Monkey.from(it) }.toMap()
    }

    val root = monkeys.get("root")!!
    println(
        root.eval(monkeys)
    )
}

sealed interface Monkey {
    fun eval(ms: Map<String, Monkey>): Long

    data class Num(val n: Long) : Monkey {
        override fun eval(ms: Map<String, Monkey>): Long = n
    }

    data class Add(val a: String, val b: String) : Monkey {
        override fun eval(ms: Map<String, Monkey>): Long =
            ms[a]!!.eval(ms) + ms[b]!!.eval(ms)
    }

    data class Sub(val a: String, val b: String) : Monkey {
        override fun eval(ms: Map<String, Monkey>): Long =
            ms[a]!!.eval(ms) - ms[b]!!.eval(ms)
    }

    data class Mult(val a: String, val b: String) : Monkey {
        override fun eval(ms: Map<String, Monkey>): Long =
            ms[a]!!.eval(ms) * ms[b]!!.eval(ms)
    }

    data class Div(val a: String, val b: String) : Monkey {
        override fun eval(ms: Map<String, Monkey>): Long =
            ms[a]!!.eval(ms) / ms[b]!!.eval(ms)
    }

    companion object {
        val numPattern = "([a-z]+): (\\d+)".toRegex()
        val opPattern = "([a-z]+): ([a-z]+) (.) ([a-z]+)".toRegex()

        fun from(s: String): Pair<String, Monkey> {
            val numMatch = numPattern.matchEntire(s)
            if (numMatch != null) {
                return numMatch.groupValues[1] to Num(numMatch.groupValues[2].toLong())
            }

            val opMatch = opPattern.matchEntire(s)!!
            val a = opMatch.groupValues[2]
            val b = opMatch.groupValues[4]
            val op = when (opMatch.groupValues[3]) {
                "+" -> Add(a, b)
                "-" -> Sub(a, b)
                "*" -> Mult(a, b)
                "/" -> Div(a, b)
                else -> throw IllegalArgumentException("invalid '$s'")
            }
            return opMatch.groupValues[1] to op
        }
    }
}