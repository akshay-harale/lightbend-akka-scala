package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash, Timers}
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

  def props(prepareCoffeeDuration: FiniteDuration, accuracy: Int) = Props(new Barista(prepareCoffeeDuration, accuracy))
}

class Barista(prepareCoffeeDuration: FiniteDuration, accuracy: Int)
  extends Actor
    with ActorLogging
    with Timers
    with Stash {
  def receive: Receive = ready

  private def ready: Receive = {
    case PrepareCoffee(coffee, guest) =>
      timers.startSingleTimer("coffee-prepared",CoffeePrepared(pickCoffee(coffee),guest),prepareCoffeeDuration)
      context.become(busy(sender()))
  }

  private def busy(waiter: ActorRef): Receive = {
    case coffeePrepared: CoffeePrepared =>
      waiter ! coffeePrepared
      unstashAll()
      context.become(ready)
    case _ =>
      stash()
  }

  private def pickCoffee(coffee: Coffee): Coffee = {
    if (Random.nextInt(100) < accuracy) {
      coffee
    } else {
      Coffee.anyOther(coffee)
    }
  }
}
