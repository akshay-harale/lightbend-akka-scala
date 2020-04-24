package com.lightbend.training.coffeehouse

/**
 * Created by akshay on 24/04/20
 *
 * @author akshay
 *         Test
 */
object Test extends App {

  val  l = List(1,2,3,4,5)
  val sumOfEvenIndex = l.zipWithIndex.map {
    case (v, key) if (key % 2 == 0) => v
    case _ => 0
  }.sum
  println(sumOfEvenIndex)
  val  l1 = List(List(1,2,3,4,5),List(6,7))
  println(l1.flatMap(_))
}
