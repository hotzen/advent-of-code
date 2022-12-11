package day11

import util.getResourceAsFile
import java.util.*


fun main() {
    val monkeys = getResourceAsFile("day11.txt").useLines { lines ->
        val initState = Parser.State(emptyList(), MonkeyBuilder())
        lines.foldIndexed(initState) { idx, state, line ->
            Parser.parse(idx, state, line)
        }.finalize()
            .monkeys
            .associateByTo(LinkedHashMap()) { it.id }
    }
    println(monkeys)

    for (round in 1..20) {
//        println("####### ROUND $round")
        for ((monkeyId, monkey) in monkeys) {
//            println("### TURN $monkeyId")
            monkey.throwItems { item ->
                val inspectedItem = monkey.inspect(item).noDamageRelief()
                val dispatchTo = monkey.dispatcher.determineTarget(inspectedItem.worryLevel)
//                println("$item --inspect--> $inspectedItem --throw--> $dispatchTo")
                monkeys[dispatchTo]!!.items.add(inspectedItem)
            }
        }
    }

    println(
        monkeys
            .map { (monkeyId, monkey) -> monkey.inspectionCount }
            .sorted()
            .reversed()
            .take(2)
            .reduce { a, b -> a * b }
    )
}

data class Monkey(
    val id: MonkeyId,
    val items: MutableList<Item>,
    val op: MonkeyOp,
    val dispatcher: Dispatcher
) {
    var inspectionCount = 0

    fun inspect(item: Item): Item {
        inspectionCount++
        return Item(
            op.calc(item.worryLevel)
        )
    }


    fun throwItems(block: ((Item) -> Unit)) {
        var item = items.removeFirstOrNull()
        while (item != null) {
            block(item)
            item = items.removeFirstOrNull()
        }
    }
}

sealed interface MonkeyOp {
    fun calc(old: Int): Int

    data class Add(val moar: Int) : MonkeyOp {
        override fun calc(old: Int): Int = old + moar
    }

    data class Mult(val multi: Int) : MonkeyOp {
        override fun calc(old: Int): Int = old * multi
    }

    object Square : MonkeyOp {
        override fun calc(old: Int): Int = old * old
    }
}

data class Dispatcher(
    val divisor: Int,
    val ifTrue: MonkeyId,
    val ifFalse: MonkeyId
) {
    init {
        require(divisor > 0)
    }

    fun determineTarget(worryLevel: Int) =
        if (worryLevel % divisor == 0) ifTrue
        else ifFalse
}

@JvmInline
value class MonkeyId(val id: Int) {
    init {
        require(id >= 0)
    }
}

@JvmInline
value class Item(val worryLevel: Int) {
    init {
        require(worryLevel > 0)
    }

    fun noDamageRelief() = Item(
        worryLevel.div(3)
    )
}


class MonkeyBuilder {
    var monkeyId: Int = -1
    val items = mutableListOf<Item>()
    var op: MonkeyOp? = null
    var divisor: Int = -1
    var dispatchTrue: Int = -1
    var dispatchFalse: Int = -2

    fun toMonkey() = Monkey(
        MonkeyId(monkeyId),
        LinkedList(items),
        op!!,
        Dispatcher(divisor, MonkeyId(dispatchTrue), MonkeyId(dispatchFalse))
    )
}

object Parser {
    fun parse(idx: Int, state: State, line: String): State =
        when (idx % 7) {
            Index._0_Monkey -> {
                val groups = Patterns.monkey.matchEntire(line)!!.groupValues
                state.mutateBuilder { it.monkeyId = groups[1].toInt() }
            }
            Index._1_Items -> {
                val groups = Patterns.items.matchEntire(line)!!.groupValues
                state.mutateBuilder { builder ->
                    groups[1].split(",").forEach {
                        builder.items.add(Item(it.trim().toInt()))
                    }
                }
            }
            Index._2_Op -> {
                state.mutateBuilder { it.op = parseOp(line) }
            }
            Index._3_Divisor -> {
                val groups = Patterns.divisor.matchEntire(line)!!.groupValues
                state.mutateBuilder { it.divisor = groups[1].toInt() }
            }
            Index._4_True -> {
                val groups = Patterns.dispatchTrue.matchEntire(line)!!.groupValues
                state.mutateBuilder { it.dispatchTrue = groups[1].toInt() }
            }
            Index._5_False -> {
                val groups = Patterns.dispatchFalse.matchEntire(line)!!.groupValues
                state.mutateBuilder { it.dispatchFalse = groups[1].toInt() }
            }
            Index._6_Separator -> state.finalize()
            else -> throw IllegalStateException("illegal index $idx")
        }

    fun parseOp(line: String): MonkeyOp {
        if (Patterns.opSquare.matches(line)) {
            return MonkeyOp.Square
        }
        Patterns.opAdd.matchEntire(line)?.let {
            return MonkeyOp.Add(it.groupValues[1].toInt())
        }
        Patterns.opMult.matchEntire(line)?.let {
            return MonkeyOp.Mult(it.groupValues[1].toInt())
        }
        throw IllegalArgumentException("cant parse op from '$line'")
    }

    data class State(
        val monkeys: List<Monkey>,
        val builder: MonkeyBuilder
    ) {
        fun mutateBuilder(mut: ((MonkeyBuilder) -> Unit)): State {
            mut(builder)
            return this
        }

        fun finalize() = State(
            this.monkeys + this.builder.toMonkey(),
            MonkeyBuilder()
        )
    }

    object Index {
        val _0_Monkey: Int = 0
        val _1_Items: Int = 1
        val _2_Op: Int = 2
        val _3_Divisor: Int = 3
        val _4_True: Int = 4
        val _5_False: Int = 5
        val _6_Separator: Int = 6
    }

    object Patterns {
        val monkey = "Monkey (\\d+):".toRegex()
        val items = "\\s*Starting items: (.+)".toRegex()
        val opAdd = "\\s*Operation: new = old \\+ (\\d+)".toRegex()
        val opMult = "\\s*Operation: new = old \\* (\\d+)".toRegex()
        val opSquare = "\\s*Operation: new = old \\* old".toRegex()
        val divisor = "\\s*Test: divisible by (\\d+)".toRegex()
        val dispatchTrue = "\\s*If true: throw to monkey (\\d+)".toRegex()
        val dispatchFalse = "\\s*If false: throw to monkey (\\d+)".toRegex()
    }
}

