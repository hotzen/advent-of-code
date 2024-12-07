package day07

import scala.io.Source
import scala.collection.mutable
import aoc.splitIntoTuple

val in = Source
  .fromFile("input/day07.txt")
  .mkString
  .trim
  .linesIterator
  .map(_.splitIntoTuple("\\:"))
  .map(tpl => (tpl._1.toLong, tpl._2.trim().split(" ").map(_.toLong).toList))
  .toList

type BinOp = (Long, Long) => Long

def concat(a: Long, b: Long): Long = (a.toString() + b.toString()).toLong

val TheOps = List[BinOp](_ + _, _ * _, concat)

@main def main(): Unit = {
  val res = in.map((exp, nums) =>
    val validResults = genOpCombinations(nums.length - 1, TheOps).count(opCombi => calc(exp, nums, opCombi))
    if (validResults > 0) exp else 0
  )
  println(res.sum())
}

def calc(exp: Long, nums: List[Long], ops: List[BinOp]): Boolean = {
  val act = ops.zip(nums.tail).foldLeft(nums.head)((accu, opNum) =>
    val (op, num) = opNum
    if (accu > exp) accu // stop
    else op(accu, num)
  )
  act == exp
}

def genOpCombinations(n: Int, ops: List[BinOp]): List[List[BinOp]] = {
  if (n == 0) List(Nil)
  else
    for {
      op <- ops
      rest <- genOpCombinations(n - 1, ops)
    } yield op :: rest
}
