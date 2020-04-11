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
  case class ApproveCoffee(coffee:Coffee,guest: ActorRef)


  def props(caffeineLimit:Int):Props = Props(new CoffeeHouse(caffeineLimit))



}

class CoffeeHouse(caffeineLimit:Int) extends Actor with ActorLogging {
  import CoffeeHouse._

  private var guestBook:Map[ActorRef,Int] = Map.empty.withDefaultValue(0)

  private val finishCoffeeDuration:FiniteDuration =
    context.system.settings.config.getDuration("coffee-house.guest.finish-coffee-duration",TimeUnit.MILLISECONDS).millis

  private val prepareCoffeeDuration:FiniteDuration =
    context.system.settings.config.getDuration("coffee-house.barista.prepare-coffee-duration",TimeUnit.MILLISECONDS).millis

  private val barista = createBarista()
  private val waiter = createWaiter()

  protected def createBarista() = {
    context.actorOf(Barista.props(prepareCoffeeDuration), "barista")
  }



  protected def createGuest(favoriteCoffee:Coffee):ActorRef =
    context.actorOf(Guest.props(waiter,favoriteCoffee,finishCoffeeDuration))
  protected def createWaiter():ActorRef =
    context.actorOf(Waiter.props(self),"waiter")


  log.debug("CoffeeHouse Open")

  def receive: Receive = {
    case CreateGuest(favoriteCoffee) =>
      val guest = createGuest(favoriteCoffee)
      guestBook += guest -> 0
      log.info(s"Guest $guest is added to guest book")
    case ApproveCoffee(coffee,guest)  if guestBook(guest) < caffeineLimit =>
      guestBook += guest -> (guestBook(guest)+1)
      log.info(s"Guest $guest caffeine count incremented")
      barista.forward(Barista.PrepareCoffee(coffee,guest))

    case ApproveCoffee(coffee,guest) =>
      log.info(s"sorry $guest but you have reached your limit")
      context.stop(guest)

  }
}
