package day15

import util.getResourceAsFile

fun main() {
	val initSeq = getResourceAsFile("day15.in").readText().trim().split(",")
	val boxes = Array<Box>(256) { Box(mutableListOf()) }
//	val hashes = steps.map { hash(it) }
//	println(hashes.sum())

	initSeq.map { step ->
		if (step.endsWith("-")) {
			val lensLabel = step.dropLast(1)
			val box = hash(lensLabel)
			boxes[box].remove(lensLabel)
			println("REM step $step => box $box: ${boxes[box]}")
		} else {
			val (lensLabel, focalLength) = step.split("=")
			val box = hash(lensLabel)
			boxes[box].put(Lens(lensLabel, focalLength.toInt()))
			println("PUT step $step => box $box: ${boxes[box]}")
		}
	}
	val focusPower = boxes.mapIndexed { boxIdx, box ->
		val boxNum = boxIdx + 1
		box.lenses.mapIndexed { lensIdx, lens ->
			val lensSlot = lensIdx + 1
			boxNum * lensSlot * lens.focalLength
		}.sum()
	}.sum()
	println("focusPower: $focusPower")

//	println(boxes.joinToString("\n"))
}

data class Box(val lenses: MutableList<Lens>) {

	fun remove(lensLabel: String) {
		lenses.removeIf { it.label == lensLabel }
	}

	fun put(lens: Lens): Unit {
		lenses.find { it.label == lens.label }
			?.let { it.focalLength = lens.focalLength }
			?: lenses.add(lens)
	}

	override fun toString(): String = """Box: ${lenses.joinToString(" ")}"""
}

data class Lens(val label: String, var focalLength: Int) {
	override fun toString(): String = "[$label $focalLength]"
}

fun hash(s: String): Int {
	var h = 0
	for (c in s) {
		h += c.code
		h *= 17
		h %= 256
	}
	return h
}
