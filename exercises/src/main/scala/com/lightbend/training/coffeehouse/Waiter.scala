package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

/**
  * Created by akshay on 22/3/20
  *
  * @author akshay
  *         Waiter
  */

object Waiter {

  case class ServeCoffee(coffee: Coffee)

  case class CoffeeServed(coffee: Coffee)

  def props(coffeeHouse: ActorRef): Props = Props(new Waiter(coffeeHouse))
}

class Waiter(coffeeHouse: ActorRef) extends Actor with ActorLogging {

  import Waiter._

  def receive: Receive = {
    case ServeCoffee(coffee) =>
      coffeeHouse ! CoffeeHouse.ApproveCoffee(coffee, sender())
    case Barista.CoffeePrepared(coffee, guest) =>
      guest ! CoffeeServed(coffee)
  }
}
