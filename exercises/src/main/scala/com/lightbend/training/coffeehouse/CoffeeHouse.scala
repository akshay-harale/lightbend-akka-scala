package com.lightbend.training.coffeehouse

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.routing.FromConfig

import scala.concurrent.duration._

/**
  * Created by akshay on 21/3/20
  *
  * @author akshay
  *         CoffeeHouse
  */

object CoffeeHouse {

  case class CreateGuest(favoriteCoffee: Coffee, guestCaffeineLimit: Int)

  case class ApproveCoffee(coffee: Coffee, guest: ActorRef)


  def props(caffeineLimit: Int): Props = Props(new CoffeeHouse(caffeineLimit))


}

class CoffeeHouse(caffeineLimit: Int) extends Actor with ActorLogging {

  import CoffeeHouse._
  import Waiter._

  override def supervisorStrategy: SupervisorStrategy = {
    val decider : SupervisorStrategy.Decider = {
      case Guest.CaffeineException => SupervisorStrategy.Stop
      case Waiter.FrustratedException(coffee,guest) =>
        barista.forward(Barista.PrepareCoffee(coffee, guest))
        SupervisorStrategy.Restart
    }
    OneForOneStrategy()(decider.orElse(super.supervisorStrategy.decider))
  }

  private var guestBook: Map[ActorRef, Int] = Map.empty.withDefaultValue(0)

  private val finishCoffeeDuration: FiniteDuration =
    context.system.settings.config.getDuration("coffee-house.guest.finish-coffee-duration", TimeUnit.MILLISECONDS).millis

  private val prepareCoffeeDuration: FiniteDuration =
    context.system.settings.config.getDuration("coffee-house.barista.prepare-coffee-duration", TimeUnit.MILLISECONDS).millis

  private val baristaAccuracy = context.system.settings.config.getInt("coffee-house.barista.accuracy")

  private val waiterMaxComplaintCount = context.system.settings.config.getInt("coffee-house.waiter.max-complaint-count")

  private val barista = createBarista()
  private val waiter = createWaiter()

  protected def createBarista() = {
    context.actorOf(FromConfig.props(Barista.props(prepareCoffeeDuration,baristaAccuracy)), "barista")
  }


  protected def createGuest(favoriteCoffee: Coffee, guestCaffeineLimit: Int): ActorRef =
    context.actorOf(Guest.props(waiter, favoriteCoffee, finishCoffeeDuration, guestCaffeineLimit))

  protected def createWaiter(): ActorRef = {
    log.info(s"setting max compaint count $waiterMaxComplaintCount")
    context.actorOf(Waiter.props(self,barista,waiterMaxComplaintCount), "waiter")
  }


  log.debug("CoffeeHouse Open")

  def receive: Receive = {
    case CreateGuest(favoriteCoffee, guestCaffeineLimit) =>
      val guest = createGuest(favoriteCoffee, guestCaffeineLimit)
      guestBook += guest -> 0
      log.info(s"Guest $guest is added to guest book")
      context.watch(guest)
    case ApproveCoffee(coffee, guest) if guestBook(guest) < caffeineLimit =>
      guestBook += guest -> (guestBook(guest) + 1)
      log.info(s"Guest $guest caffeine count incremented")
      barista.forward(Barista.PrepareCoffee(coffee, guest))

    case ApproveCoffee(coffee, guest) =>
      log.info(s"sorry $guest but you have reached your limit")
      context.stop(guest)

    case Terminated(guest) =>
      log.info(s"Thanks $guest for being our guest")
      guestBook -= guest

  }
}
