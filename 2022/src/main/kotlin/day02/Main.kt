package day02

import util.getResourceAsFile
import java.util.*

fun main(args: Array<String>) {
    val ctx = Context()
    getResourceAsFile("day02.txt").forEachLine { line(it, ctx) }

    println(
        ctx.strategyGuideRounds
            .sumOf { score(it) }
    )
}

fun line(l: String, ctx: Context) {
    val split = l.split(" ")
    ctx.strategyGuideRounds.add(
        Round(
            OpponentHand.fromSymbol(split[0]),
            DesiredOutcome.fromSymbol(split[1])
        )
    )
}

class Context {
    val strategyGuideRounds = mutableListOf<Round>()
}

data class Round(val opponent: OpponentHand, val desired: DesiredOutcome) {
    val own = OwnHand(
        when (desired.outcome) {
            Outcome.WON -> Shape.values().single { it.defeats() == opponent.shape }
            Outcome.LOSS -> opponent.shape.defeats()
            Outcome.DRAW -> opponent.shape
        }
    )
}

fun score(r: Round): Int =
    when {
        r.own.shape.defeats() == r.opponent.shape -> Outcome.WON.score + r.own.score
        r.opponent.shape.defeats() == r.own.shape -> Outcome.LOSS.score + r.own.score
        else -> Outcome.DRAW.score + r.own.score
    }

enum class Shape {
    ROCK {
        override fun defeats(): Shape = SCISSORS
    },
    PAPER {
        override fun defeats(): Shape = ROCK
    },
    SCISSORS {
        override fun defeats(): Shape = PAPER
    };

    abstract fun defeats(): Shape;
}

@JvmInline
value class OpponentHand(val shape: Shape) {
    companion object {
        fun fromSymbol(symbol: String): OpponentHand = when (symbol) {
            "A" -> OpponentHand(Shape.ROCK)
            "B" -> OpponentHand(Shape.PAPER)
            "C" -> OpponentHand(Shape.SCISSORS)
            else -> throw IllegalArgumentException("illegal OpponentHand $symbol")
        }
    }
}

data class OwnHand(val shape: Shape) {
    val score = when (shape) {
        Shape.ROCK -> 1
        Shape.PAPER -> 2
        Shape.SCISSORS -> 3
    }
}

@JvmInline
value class DesiredOutcome(val outcome: Outcome) {
    companion object {
        fun fromSymbol(symbol: String): DesiredOutcome = when (symbol) {
            "X" -> DesiredOutcome(Outcome.LOSS)
            "Y" -> DesiredOutcome(Outcome.DRAW)
            "Z" -> DesiredOutcome(Outcome.WON)
            else -> throw IllegalArgumentException("illegal OwnHand $symbol")
        }
    }
}

enum class Outcome(val score: Int) {
    WON(6),
    LOSS(0),
    DRAW(3)
}