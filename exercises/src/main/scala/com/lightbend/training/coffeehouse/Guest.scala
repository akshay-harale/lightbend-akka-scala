package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.lightbend.training.coffeehouse.Guest.CoffeeFinished

/**
  * Created by akshay on 22/3/20
  *
  * @author akshay
  *         Guest
  */

object Guest {
  case object CoffeeFinished
  def props(waiter: ActorRef,favoriteCoffee: Coffee) = Props(new Guest(waiter,favoriteCoffee))

}

class Guest(waiter: ActorRef,favoriteCoffee: Coffee) extends Actor with ActorLogging {

  private var coffeeCount:Int = 0

  def receive: Receive = {
    case Waiter.CoffeeServed(coffee) =>
      coffeeCount += 1
      log.info(s"Enjoying my $coffeeCount yummy $coffee")
    case CoffeeFinished  => waiter ! Waiter.ServeCoffee(favoriteCoffee)
  }
}
