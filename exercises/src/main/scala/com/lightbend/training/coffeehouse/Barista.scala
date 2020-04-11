package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.lightbend.training.coffeehouse.Barista.{CoffeePrepared, PrepareCoffee}

import scala.concurrent.duration.FiniteDuration

/**
  * Created by akshay on 11/4/20
  *
  * @author akshay
  *         Barista
  */
object Barista {

  case class PrepareCoffee(coffee: Coffee, guest: ActorRef)

  case class CoffeePrepared(coffee: Coffee, guest: ActorRef)

  def props(prepareCoffeeDuration: FiniteDuration) = Props(new Barista(prepareCoffeeDuration))
}

class Barista(prepareCoffeeDuration: FiniteDuration) extends Actor with ActorLogging {
  def receive: Receive = {
    case PrepareCoffee(coffee, guest) =>
      busy(prepareCoffeeDuration)
      sender() ! CoffeePrepared(coffee, guest)
  }
}
