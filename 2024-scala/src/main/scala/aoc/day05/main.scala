package day05

import scala.io.Source
import scala.collection.mutable
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

@main def part1(): Unit = {
  val orderMap = orders
    .flatMap(tpl =>
      List(
        tpl._1 -> tpl,
        tpl._2 -> tpl
      )
    )
    .groupMap(_._1)(_._2)

  val res = updates.flatMap(updatedPages =>
    val updatedPagesMap = updatedPages.zipWithIndex.toMap
    val relevantOrders = updatedPages.flatMap(page => orderMap.get(page))

    val checkedOrders = for (
      orderSpecs <- relevantOrders;
      orderSpec <- orderSpecs
    ) yield {
      val maybeIdx1 = updatedPagesMap.get(orderSpec._1)
      val maybeIdx2 = updatedPagesMap.get(orderSpec._2)
      (maybeIdx1, maybeIdx2) match {
        case (Some(aIdx), Some(bIdx)) => aIdx < bIdx
        case (Some(_), None)          => true
        case (None, Some(_))          => true
        case (None, None)             => true
      }
    }

    if (checkedOrders.forall(identity))
      Some(updatedPages.midElem())
    else
      None
  )

  // println(res.mkString(","))
  println(res.sum())
}
