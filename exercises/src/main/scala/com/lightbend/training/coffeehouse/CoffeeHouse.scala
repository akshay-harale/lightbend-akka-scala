package com.lightbend.training.coffeehouse

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.concurrent.duration._

/**
  * Created by akshay on 21/3/20
  *
  * @author akshay
  *         CoffeeHouse
  */

object CoffeeHouse {

  case class CreateGuest(favoriteCoffee:Coffee)

  def props:Props = Props(new CoffeeHouse)



}

class CoffeeHouse extends Actor with ActorLogging {
  import CoffeeHouse._

  private val finishCoffeeDuration:FiniteDuration =
    context.system.settings.config.getDuration("coffee-house.guest.finish-coffee-duration",TimeUnit.MILLISECONDS).millis

  private val prepareCoffeeDuration:FiniteDuration =
    context.system.settings.config.getDuration("coffee-house.barista.prepare-coffee-duration",TimeUnit.MILLISECONDS).millis

  private val barista = createBarista()
  private val waiter = createWaiter()

  protected def createBarista() = {
    context.actorOf(Barista.props(prepareCoffeeDuration), "barista")
  }



  protected def createGuest(favoriteCoffee:Coffee):ActorRef = context.actorOf(Guest.props(waiter,favoriteCoffee,finishCoffeeDuration))
  protected def createWaiter():ActorRef = context.actorOf(Waiter.props(barista),"waiter")


  log.debug("CoffeeHouse Open")

  def receive: Receive = {
    case CreateGuest(favoriteCoffee) => createGuest(favoriteCoffee)
  }
}
