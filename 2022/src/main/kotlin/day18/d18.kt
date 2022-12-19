package day18

import util.getResourceAsFile

fun main() {
    val cubes = getResourceAsFile("day18.txt").useLines { lines ->
        lines.map { line ->
            val ps = line.split(",")
            Cube(ps[0].toInt(), ps[1].toInt(), ps[2].toInt())
        }.toSet()
    }
    println(cubes)

    part1(cubes)
}

fun part1(cubes: Set<Cube>) {
    for (cube in cubes) {
        for (side in cube.sides()) {
            if (cubes.contains(side)) {
                cube.covered++
            }
        }
    }

    println(
        cubes.sumOf { it.uncovered() }
    )
}

data class Cube(val x: Int, val y: Int, val z: Int) {
    var covered: Int = 0

    fun uncovered(): Int = 6 - covered

    fun sides(): Set<Cube> = setOf(
        Cube(x + 1, y, z),
        Cube(x - 1, y, z),
        Cube(x, y + 1, z),
        Cube(x, y - 1, z),
        Cube(x, y, z + 1),
        Cube(x, y, z - 1),
    )
}