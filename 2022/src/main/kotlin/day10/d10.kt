package day10

import util.getResourceAsFile

data class CycleContext(
    val cycle: Int,
    val registerValue: Int,
    val signalStrengthPendingCycles: List<Int>,
    val signalStrengths: List<Int>
)

fun main() {
    getResourceAsFile("day10.txt").useLines { lines ->
        val initialContext = CycleContext(
            cycle = 0,
            registerValue = 1,
            signalStrengthPendingCycles = listOf(20, 60, 100, 140, 180, 220),
            signalStrengths = emptyList()
        )

        val ctx = lines.fold(initialContext) { ctx, line ->
            processLine(ctx, line)
        }

        println(ctx.signalStrengths)
        println(ctx.signalStrengths.sum())
    }
}

fun processLine(ctx: CycleContext, line: String): CycleContext {
    // short circuit
    if (ctx.signalStrengthPendingCycles.isEmpty()) {
        println("ABORTING IN CYCLE ${ctx.cycle} with ${ctx.registerValue}")
        return ctx
    }

    val nextSignalStrengthCycle = ctx.signalStrengthPendingCycles.first()
    val op = Op.from(line)

    val endsWithCycle = ctx.cycle + op.duration()
    val endsWithValue = op.change(ctx.registerValue)

    println("V${ctx.registerValue} @ C${ctx.cycle}: $op -> V$endsWithValue @ C$endsWithCycle")

    return if (endsWithCycle == nextSignalStrengthCycle) {
//        val signalStrength = endsWithCycle * endsWithValue
        val signalStrength = endsWithCycle * ctx.registerValue
        CycleContext(
            cycle = endsWithCycle,
            registerValue = endsWithValue,
            signalStrengthPendingCycles = ctx.signalStrengthPendingCycles.drop(1),
            signalStrengths = ctx.signalStrengths + signalStrength
        )
    } else if (endsWithCycle > nextSignalStrengthCycle) {
        val signalStrength = nextSignalStrengthCycle * ctx.registerValue
        CycleContext(
            cycle = endsWithCycle,
            registerValue = endsWithValue,
            signalStrengthPendingCycles = ctx.signalStrengthPendingCycles.drop(1),
            signalStrengths = ctx.signalStrengths + signalStrength
        )
    } else {
        CycleContext(
            cycle = endsWithCycle,
            registerValue = endsWithValue,
            signalStrengthPendingCycles = ctx.signalStrengthPendingCycles,
            signalStrengths = ctx.signalStrengths
        )
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