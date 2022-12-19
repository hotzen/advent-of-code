package day19

import util.getResourceAsFile

fun main() {
    val blueprints = getResourceAsFile("day19.test").useLines { lines ->
        lines.map { RobotFactoryBlueprint.from(it) }.toList()
    }

    part1(blueprints)
}

fun part1(blueprints: List<RobotFactoryBlueprint>) {

//    val minerals = Minerals()
//    for (minute in 1..24) {
//
//
//    }
}
//
//fun findBlueprintMaxGeodes(blue: RobotFactoryBlueprint): Int {
//
//}

fun tryBuildRobot(blue: RobotFactoryBlueprint, r: Robot, avail: Minerals): Minerals? =
    when (r) {
        Robot.GEODE -> {
            val (oreCosts, obsidianCosts) = blue.geodeRobotCosts
            if (oreCosts <= avail.ore && obsidianCosts <= avail.obsidian) {
                Minerals(
                    ore = avail.ore - oreCosts,
                    clay = avail.clay,
                    obsidian = avail.obsidian - obsidianCosts,
                    geode = avail.geode
                )
            } else null
        }
        Robot.OBSIDIAN -> {
            val (oreCosts, clayCosts) = blue.obsidianRobotCosts
            if (oreCosts <= avail.ore && clayCosts <= avail.clay) {
                Minerals(
                    ore = avail.ore - oreCosts,
                    clay = avail.clay - clayCosts,
                    obsidian = avail.obsidian,
                    geode = avail.geode
                )
            } else null
        }
        Robot.CLAY -> {
            val oreCosts = blue.clayRobotCosts
            if (oreCosts <= avail.ore) {
                Minerals(
                    ore = avail.ore - oreCosts,
                    clay = avail.clay,
                    obsidian = avail.obsidian,
                    geode = avail.geode
                )
            } else null
        }
        Robot.ORE -> {
            val oreCosts = blue.oreRobotCosts
            if (oreCosts <= avail.ore) {
                Minerals(
                    ore = avail.ore - oreCosts,
                    clay = avail.clay,
                    obsidian = avail.obsidian,
                    geode = avail.geode
                )
            } else null
        }
    }

fun collect(r: Robot, ms: Minerals): Minerals =
    when (r) {
        Robot.GEODE -> Minerals(
            ore = ms.ore,
            clay = ms.clay,
            obsidian = ms.obsidian,
            geode = ms.geode + 1
        )
        Robot.OBSIDIAN -> Minerals(
            ore = ms.ore,
            clay = ms.clay,
            obsidian = ms.obsidian + 1,
            geode = ms.geode
        )
        Robot.CLAY -> Minerals(
            ore = ms.ore,
            clay = ms.clay + 1,
            obsidian = ms.obsidian,
            geode = ms.geode
        )
        Robot.ORE -> Minerals(
            ore = ms.ore + 1,
            clay = ms.clay,
            obsidian = ms.obsidian,
            geode = ms.geode
        )
    }

data class Minerals(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0
)
//{
////    operator fun plus(other: Minerals) = Minerals(
////        ore = this.ore + other.ore,
////        clay = this.clay + other.clay,
////        obsidian = this.obsidian + other.obsidian,
////        geode = this.geode + other.geode,
////    )
////
////    operator fun minus(other: Minerals) = Minerals(
////        ore = this.ore - other.ore,
////        clay = this.clay - other.clay,
////        obsidian = this.obsidian - other.obsidian,
////        geode = this.geode - other.geode,
////    )
//}

enum class Robot {
    GEODE,
    OBSIDIAN,
    CLAY,
    ORE;
}

data class RobotFactoryBlueprint(
    val id: Int,
    val oreRobotCosts: Int,
    val clayRobotCosts: Int,
    val obsidianRobotCosts: Pair<Int, Int>,
    val geodeRobotCosts: Pair<Int, Int>
) {
    companion object {
        val pattern =
            "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.".toRegex()

        fun from(s: String): RobotFactoryBlueprint {
            val groups = pattern.matchEntire(s)!!.groupValues
            return RobotFactoryBlueprint(
                groups[1].toInt(),
                groups[2].toInt(),
                groups[3].toInt(),
                Pair(groups[4].toInt(), groups[5].toInt()),
                Pair(groups[6].toInt(), groups[7].toInt()),
            )
        }
    }
}

