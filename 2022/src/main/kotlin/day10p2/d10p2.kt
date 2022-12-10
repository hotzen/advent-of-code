package day10p2

import util.getResourceAsFile


fun main() {
    val ops = getResourceAsFile("day10.txt").useLines { lines ->
        lines.map { Op.from(it) }.toList()
    }

    val opsByCycle = ops.fold(OpCycleAccu()) { accu, op ->
        val endCycle = accu.cycle + op.duration()
        OpCycleAccu(
            cycle = endCycle,
            opsByCycle = accu.opsByCycle + Pair(endCycle, op)
        )
    }.opsByCycle

    val crt = CRT(40, 6)
    val initState = CycleState(
        registerValue = 1,
        sprite = setOf(0, 1, 2)
    )
    val cycles = 1..240
    cycles.fold(initState) { state, cycle ->
        val crtRegisterCol = crt.cycleToRegisterCol(cycle)
        val visible = state.sprite.contains(crtRegisterCol)
        crt.putCycle(cycle, visible)

        val newRegisterValue = opsByCycle[cycle]
            ?.change(state.registerValue) // if cycle ended with an op, use op to update value
            ?: state.registerValue // no op finished in this cycle, keep value as-is

        val newSprite = setOf(newRegisterValue, newRegisterValue - 1, newRegisterValue + 1)
        CycleState(newRegisterValue, newSprite)
    }

    println(
        crt
    )
}

data class OpCycleAccu(
    val cycle: Int = 0,
    val opsByCycle: Map<Int, Op> = emptyMap()
)

data class CycleState(
    val registerValue: Int,
    val sprite: Set<Int>
)

data class CRT(val cols: Int, val rows: Int) {
    val pixels = BooleanArray(cols * rows)

    fun cycleToRegisterCol(cycle: Int): Int =
        (cycle - 1) % cols

    fun putCycle(cycle: Int, enabled: Boolean) {
        val idx = cycle - 1
        pixels[idx] = enabled
    }

    override fun toString(): String {
        val s = StringBuffer()
        for (i in 0 until pixels.size) {
            if (i % cols == 0) s.append("\n")
            val enabled = pixels.getOrNull(i) ?: false
            s.append(if (enabled) '#' else '.')
        }
        return s.toString()
    }
}

sealed interface Op {
    fun duration(): Int = when (this) {
        is Noop -> 1
        is AddX -> 2
    }

    fun change(value: Int) = when (this) {
        is Noop -> value
        is AddX -> value + this.num
    }

    object Noop : Op
    data class AddX(val num: Int) : Op

    companion object {
        fun from(s: String): Op {
            if (s == "noop") {
                return Noop
            }
            val parts = s.split(" ")
            if (parts[0] == "addx") {
                return AddX(parts[1].toInt())
            }
            throw IllegalArgumentException("invalid Op '$s'")
        }
    }
}