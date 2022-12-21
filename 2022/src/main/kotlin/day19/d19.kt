package day19

import util.getResourceAsFile
import java.lang.Math.max

fun main() {
    val blueprints = getResourceAsFile("day19.txt").useLines { lines ->
        lines.map { RobotFactoryBlueprint.from(it) }.toList()
    }

    part1(blueprints)
}

val memoized = mutableMapOf<Args, Int>()

fun part1(blueprints: List<RobotFactoryBlueprint>) {
    val minutesLeft = 24
    val mats = Minerals()
    val active = ActiveRobots(oreRobots = 1)

    val qualities = blueprints.map { blueprint ->
        memoized.clear()
        val maxGeodes = solve(Args(blueprint, minutesLeft, mats, active, null))
        println("blueprint ${blueprint.id}: $maxGeodes")
        blueprint.id * maxGeodes
    }
    println(qualities)
    println(qualities.sum())
}

data class Args(
    val blueprint: RobotFactoryBlueprint,
    val minsLeft: Int,
    val mats: Minerals,
    val robots: ActiveRobots,
    val buildNextRobot: Robot?
)

fun solve(args: Args): Int =
    memoized.getOrPut(args) {
        _solve(args)
    }

fun _solve(args: Args): Int {
    if (args.minsLeft <= 0) {
        return args.mats.geode
    }

//    println("### BLUEPRINT ${blueprint.id} / t-m $minsLeft / $mats $robots")
    var newMats = args.mats
    var newRobots = args.robots

    if (args.buildNextRobot != null) {
        val (matsAfterBuilt, robotsAfterBuilt) = tryBuildRobot(
            args.blueprint,
            args.buildNextRobot,
            args.mats,
            args.robots
        )!!
//        println("BUILD $buildNextRobot / BEFORE $mats -> AFTER $matsAfterBuilt / BEFORE $robots -> AFTER $robotsAfterBuilt")

        newMats = collectResources(args.robots, matsAfterBuilt)
//        println("collected: $newMats")

        newRobots = robotsAfterBuilt

    } else {
//        println("buildNextRobot NULL, skipping building...")

        newMats = collectResources(args.robots, args.mats)
//        println("collected: $newMats")
    }

    // no further building
    val noBuilding = solve(
        Args(
            args.blueprint,
            args.minsLeft - 1,
            newMats,
            newRobots,
            null
        )
    )

    // building something
    val buildOptions =
        determineBuildOptions(args.blueprint, newMats, args.robots, args.minsLeft) // use OLD active robots!
//    println("build options: $buildOptions")

    return if (buildOptions.isEmpty()) {
        noBuilding
    } else {
        val buildMax = buildOptions.map { buildRobot ->
            solve(
                Args(
                    args.blueprint,
                    args.minsLeft - 1,
                    newMats,
                    newRobots,
                    buildRobot
                )
            )
        }.max()

        max(noBuilding, buildMax)
    }
}

fun determineBuildOptions(
    blueprint: RobotFactoryBlueprint,
    mats: Minerals,
    robots: ActiveRobots,
    minsLeft: Int
): Set<Robot> {
    val possibleBuilds = Robot.values().mapNotNull { robot ->
        tryBuildRobot(blueprint, robot, mats, robots)?.let { robot }
    }

    // focus on geodes
    if (possibleBuilds.contains(Robot.GEODE)) {
        return setOf(Robot.GEODE)
    }

    // when to build an obs?
    if (possibleBuilds.contains(Robot.OBS) && robots.obsRobots < blueprint.geodeRobotCosts.second) {
        return setOf(Robot.OBS)
    }

    // do we need more ore or clay?
    val geodeOreClayReqs = requiredTotalOreClayForGeodeRobot(blueprint)
    return buildSet {
        if (possibleBuilds.contains(Robot.ORE) && robots.oreRobots < blueprint.oreRobotCosts) {
            add(Robot.ORE)
        }
        if (possibleBuilds.contains(Robot.ORE) && robots.oreRobots < blueprint.clayRobotCosts) {
            add(Robot.ORE)
        }
        if (possibleBuilds.contains(Robot.ORE) && robots.oreRobots < blueprint.obsRobotCosts.first) {
            add(Robot.ORE)
        }
//        if (possibleBuilds.contains(Robot.ORE) && robots.oreRobots < geodeOreClayReqs.ore) {
//            add(Robot.ORE)
//        }

        if (possibleBuilds.contains(Robot.CLAY) && robots.clayRobots < blueprint.obsRobotCosts.second) {
            add(Robot.CLAY)
        }
        if (possibleBuilds.contains(Robot.CLAY) && robots.clayRobots < geodeOreClayReqs.clay) {
            add(Robot.CLAY)
        }
    }
}

fun requiredTotalOreClayForGeodeRobot(blueprint: RobotFactoryBlueprint): Minerals {
    val reqGeode = requiredMats(blueprint, Robot.GEODE)
    val reqObs = requiredMats(blueprint, Robot.OBS)

    return Minerals(
        ore = reqGeode.ore + (reqGeode.obs * reqObs.ore),
        clay = reqGeode.obs * reqObs.clay
    )
}

fun requiredMats(blueprint: RobotFactoryBlueprint, robot: Robot): Minerals =
    when (robot) {
        Robot.GEODE -> {
            val (oreCosts, obsCosts) = blueprint.geodeRobotCosts
            Minerals(
                ore = oreCosts,
                obs = obsCosts,
            )
        }

        Robot.OBS -> {
            val (oreCosts, clayCosts) = blueprint.obsRobotCosts
            Minerals(
                ore = oreCosts,
                clay = clayCosts
            )
        }

        Robot.CLAY -> {
            Minerals(
                ore = blueprint.clayRobotCosts
            )
        }

        Robot.ORE -> {
            Minerals(
                ore = blueprint.oreRobotCosts
            )
        }
    }

fun tryBuildRobot(
    blue: RobotFactoryBlueprint,
    robot: Robot,
    mats: Minerals,
    active: ActiveRobots
): Pair<Minerals, ActiveRobots>? {
    val reqMats = requiredMats(blue, robot)
    val matsLeft = mats - reqMats
//    println("trying to build $robot / required: $reqMats / available: $mats / after build: $matsLeft")
    return if (matsLeft.anyNegative())
        null
    else {
        val newActive = active.apply(robot) { count -> count + 1 }
        matsLeft to newActive
    }
}

fun collectResources(rs: ActiveRobots, mats: Minerals): Minerals =
    collectResources(
        Robot.ORE, rs.oreRobots,
        collectResources(
            Robot.CLAY, rs.clayRobots,
            collectResources(
                Robot.OBS, rs.obsRobots,
                collectResources(Robot.GEODE, rs.geodeRobots, mats)
            )
        )
    )

fun collectResources(robot: Robot, n: Int, mats: Minerals): Minerals =
    mats.apply(robot) { amount -> amount + n }

data class ActiveRobots(
    val oreRobots: Int = 0,
    val clayRobots: Int = 0,
    val obsRobots: Int = 0,
    val geodeRobots: Int = 0
) {
    fun apply(robot: Robot, fn: ((Int) -> Int)) =
        when (robot) {
            Robot.GEODE -> ActiveRobots(
                oreRobots = this.oreRobots,
                clayRobots = this.clayRobots,
                obsRobots = this.obsRobots,
                geodeRobots = fn(this.geodeRobots),
            )

            Robot.OBS -> ActiveRobots(
                oreRobots = this.oreRobots,
                clayRobots = this.clayRobots,
                obsRobots = fn(this.obsRobots),
                geodeRobots = this.geodeRobots,
            )

            Robot.CLAY -> ActiveRobots(
                oreRobots = this.oreRobots,
                clayRobots = fn(this.clayRobots),
                obsRobots = this.obsRobots,
                geodeRobots = this.geodeRobots,
            )

            Robot.ORE -> ActiveRobots(
                oreRobots = fn(this.oreRobots),
                clayRobots = this.clayRobots,
                obsRobots = this.obsRobots,
                geodeRobots = this.geodeRobots,
            )
        }
}

data class Minerals(
    val ore: Int = 0,
    val clay: Int = 0,
    val obs: Int = 0,
    val geode: Int = 0
) {
    operator fun plus(other: Minerals) = Minerals(
        ore = this.ore + other.ore,
        clay = this.clay + other.clay,
        obs = this.obs + other.obs,
        geode = this.geode + other.geode,
    )

    operator fun minus(other: Minerals) = Minerals(
        ore = this.ore - other.ore,
        clay = this.clay - other.clay,
        obs = this.obs - other.obs,
        geode = this.geode - other.geode,
    )

    fun anyNegative(): Boolean =
        ore < 0 || clay < 0 || obs < 0 || geode < 0

    fun apply(robot: Robot, fn: ((Int) -> Int)) =
        when (robot) {
            Robot.GEODE -> Minerals(
                ore = this.ore,
                clay = this.clay,
                obs = this.obs,
                geode = fn(this.geode),
            )

            Robot.OBS -> Minerals(
                ore = this.ore,
                clay = this.clay,
                obs = fn(this.obs),
                geode = this.geode,
            )

            Robot.CLAY -> Minerals(
                ore = this.ore,
                clay = fn(this.clay),
                obs = this.obs,
                geode = this.geode,
            )

            Robot.ORE -> Minerals(
                ore = fn(this.ore),
                clay = this.clay,
                obs = this.obs,
                geode = this.geode,
            )
        }
}

enum class Robot { GEODE, OBS, CLAY, ORE } // build order

data class RobotFactoryBlueprint(
    val id: Int,
    val oreRobotCosts: Int,
    val clayRobotCosts: Int,
    val obsRobotCosts: Pair<Int, Int>, // ORE & CLAY
    val geodeRobotCosts: Pair<Int, Int> // ORE & OBS
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

