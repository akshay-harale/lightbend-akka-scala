package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.lightbend.training.coffeehouse.Barista.{CoffeePrepared, PrepareCoffee}

import scala.concurrent.duration.FiniteDuration
import scala.util.Random

/**
  * Created by akshay on 11/4/20
  *
  * @author akshay
  *         Barista
  */
object Barista {

  case class PrepareCoffee(coffee: Coffee, guest: ActorRef)

  case class CoffeePrepared(coffee: Coffee, guest: ActorRef)

  def props(prepareCoffeeDuration: FiniteDuration, accuracy:Int) = Props(new Barista(prepareCoffeeDuration, accuracy))
}

class Barista(prepareCoffeeDuration: FiniteDuration, accuracy:Int) extends Actor with ActorLogging {
  def receive: Receive = {
    case PrepareCoffee(coffee, guest) =>
      busy(prepareCoffeeDuration)
      sender() ! CoffeePrepared(pickCoffee(coffee), guest)
  }

  private def pickCoffee(coffee: Coffee): Coffee = {
    if(Random.nextInt(100)<accuracy) {
      coffee
    } else {
      Coffee.anyOther(coffee)
    }
  }
}
