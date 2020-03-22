package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

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

  private val waiter = createWaiter()

  protected def createGuest(favoriteCoffee:Coffee):ActorRef = context.actorOf(Guest.props(waiter,favoriteCoffee))
  protected def createWaiter():ActorRef = context.actorOf(Waiter.props(),"waiter")


  log.debug("CoffeeHouse Open")

  def receive: Receive = {
    case CreateGuest(favoriteCoffee) => createGuest(favoriteCoffee)
  }
}
