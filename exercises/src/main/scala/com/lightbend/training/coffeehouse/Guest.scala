package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import com.lightbend.training.coffeehouse.Guest.CoffeeFinished

import scala.concurrent.duration.FiniteDuration

/**
  * Created by akshay on 22/3/20
  *
  * @author akshay
  *         Guest
  */

object Guest {

  case object CoffeeFinished

  def props(waiter: ActorRef, favoriteCoffee: Coffee, finishCoffeeDuration: FiniteDuration) =
    Props(new Guest(waiter, favoriteCoffee, finishCoffeeDuration))

}

class Guest(
             waiter: ActorRef,
             favoriteCoffee: Coffee,
             finishCoffeeDuration: FiniteDuration) extends Actor with ActorLogging with Timers {

  private var coffeeCount: Int = 0

  orderCoffee()

  def receive: Receive = {
    case Waiter.CoffeeServed(coffee) =>
      coffeeCount += 1

      log.info(s"Enjoying my $coffeeCount yummy $coffee $finishCoffeeDuration")

      timers.startSingleTimer("coffee-finished", CoffeeFinished, finishCoffeeDuration)

    case CoffeeFinished =>
      orderCoffee()
  }


  override def postStop(): Unit = {
    log.info("Goof bye")
    super.postStop()
  }

  private def orderCoffee() = {
    waiter ! Waiter.ServeCoffee(favoriteCoffee)
  }

}
