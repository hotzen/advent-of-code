package day05

import scala.io.Source
import aoc.splitIntoTuple
import aoc.splitIntoIntTuple
import aoc.midElem

val in = Source
  .fromFile("input/day05.txt")
  .mkString
  .trim
  .split("\n\n")

val (orders, updates) = (
  in(0).linesIterator.map(_.splitIntoIntTuple("\\|")).toList,
  in(1).linesIterator.map(_.split("\\,").map(_.toInt)).toList
)

val orderMap = orders
  .flatMap(tpl => List(tpl._1 -> tpl, tpl._2 -> tpl))
  .groupMap(_._1)(_._2)

def pagesSatisfyOrders(pages: List[Int]): Boolean = {
  val updatedPagesMap = pages.zipWithIndex.toMap
  val relevantOrders = pages.flatMap(page => orderMap.get(page)).flatten

  relevantOrders.forall(orderSpec =>
    val maybeIdx1 = updatedPagesMap.get(orderSpec._1)
    val maybeIdx2 = updatedPagesMap.get(orderSpec._2)
    (maybeIdx1, maybeIdx2) match {
      case (Some(aIdx), Some(bIdx)) => aIdx < bIdx
      case (Some(_), None)          => true
      case (None, Some(_))          => true
      case (None, None)             => true
    }
  )
}

@main def part1(): Unit = {
  val res = updates.flatMap(pages =>
    if (pagesSatisfyOrders(pages.toList))
      Some(pages.midElem)
    else
      None
  )
  println(res.sum())
}

@main def part2(): Unit = {
  val res = updates.flatMap(pages =>
    val relevantOrders =
      pages.flatMap(page => orderMap.get(page)).flatten.toList

    if (!pagesSatisfyOrders(pages.toList))
      Some(
        pages.sortWith((a, b) => relevantOrders.contains(a, b))
      )
    else
      None
  )
  println(res.map(_.toArray.midElem).sum())
}
