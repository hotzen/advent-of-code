package day02

import scala.io.Source
import java.util.LinkedList
import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer
import scala.collection.mutable

val in = Source
  .fromFile("input/day02.txt")
  .mkString
  .trim()
  .linesIterator

val safeRange = 1 to 3

def isSafe(levels: Array[Int]): Boolean = {
    val incr = levels(1) > levels(0)
    levels.sliding(2).forall {
      case Array(a, b) => 
        (if (incr) b > a else a > b)
        &&
          (safeRange contains (a - b).abs)
    }
  }

@main def part1(): Unit = {
  val reports = in.map(_.split(" ").map(_.toInt)).toList
  // println(reports.map(_.mkString(", ")).mkString("\n"))

  println(
    reports.count(isSafe(_))
  )
}

case class Report(val levels: Array[Int]) 

case class DampenedReport(
  val original: Array[Int],
  val dampened: List[Array[Int]]
) {
  def hasAnySafe(): Boolean = {
      (original :: dampened).exists(isSafe(_))
  }
}

@main def part2(): Unit = {
  val reports = in.map(line =>
    Report(line.split(" ").map(_.toInt))
  ).toList
  val safeRange = 1 to 3

  val dampenedReports = reports.map(report =>
    val dampenedLevels = for (dropIdx <- 0 until report.levels.length) yield {
       report.levels.zipWithIndex.collect {
         case (level, idx) if idx != dropIdx => level
       }.toArray
    }
    DampenedReport(report.levels, dampenedLevels.toList)
  )
  
  println(
    dampenedReports.count(_.hasAnySafe())
  )
}
