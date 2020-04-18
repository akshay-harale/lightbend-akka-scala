package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}
import com.lightbend.training.coffeehouse.Guest.{CaffeineException, CoffeeFinished}

import scala.concurrent.duration.FiniteDuration

/**
  * Created by akshay on 22/3/20
  *
  * @author akshay
  *         Guest
  */

object Guest {

  case object CoffeeFinished

  case object CaffeineException extends IllegalStateException

  def props(waiter: ActorRef, favoriteCoffee: Coffee, finishCoffeeDuration: FiniteDuration, caffeineLimit:Int) =
    Props(new Guest(waiter, favoriteCoffee, finishCoffeeDuration,caffeineLimit))

}

class Guest(
             waiter: ActorRef,
             favoriteCoffee: Coffee,
             finishCoffeeDuration: FiniteDuration,
             caffeineLimit:Int) extends Actor with ActorLogging with Timers {

  private var coffeeCount: Int = 0

  orderCoffee()

  def receive: Receive = {
    case Waiter.CoffeeServed(`favoriteCoffee`) =>
      coffeeCount += 1

      log.info(s"Enjoying my $coffeeCount yummy $favoriteCoffee $finishCoffeeDuration")

      timers.startSingleTimer("coffee-finished", CoffeeFinished, finishCoffeeDuration)

    case Waiter.CoffeeServed(otherCoffee) =>
      log.info(s"Excpected my $favoriteCoffee but got $otherCoffee! ")
      waiter ! Waiter.Complaint(favoriteCoffee)

    case CoffeeFinished if(coffeeCount > caffeineLimit) =>
      throw CaffeineException
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
