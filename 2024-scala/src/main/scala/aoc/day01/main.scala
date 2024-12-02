package day01

import scala.io.Source
import java.util.LinkedList
import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer
import scala.collection.mutable

val in = Source
  .fromFile("input/day01.txt")
  .mkString
  .trim()
  .linesIterator

def part1(): Unit = {
  val init = (
    List[Int](),
    List[Int]()
  )
  val (lefts, rights) = in.foldLeft(init) {
    case ((left, right), line) =>
      val xs = line.split("   ").map(_.toInt)
      (xs(0) :: left, xs(1) :: right)
  }

  val dist = lefts.sorted.zip(rights.sorted).map((l, r) => (l - r).abs).sum
  println(dist)
}

@main def part2(): Unit = {
  val init = (
    List[Int](),
    List[Int]()
  )
  val (lefts, rights) = in.foldLeft(init) {
    case ((left, right), line) =>
      val xs = line.split("   ").map(_.toInt)
      (xs(0) :: left, xs(1) :: right)
  }

  val init2 = (
    List[Int](),
    Map[Int, Int]() // cache num => score
  )
  val scoredLefts = lefts.foldLeft(init2) {
    case ((res, cache), x) if cache.contains(x) =>
      (cache(x) :: res, cache)
    case ((res, cache), x) => {
      val score = x * rights.count(_ == x)
      (score :: res, cache + (x -> score))
    }
  }._1

  println(scoredLefts.sum())
}
