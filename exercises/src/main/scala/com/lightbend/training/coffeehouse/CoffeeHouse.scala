package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by akshay on 21/3/20
  *
  * @author akshay
  *         CoffeeHouse
  */

object CoffeeHouse {

  def props:Props = Props(new CoffeeHouse)

}

class CoffeeHouse extends Actor with ActorLogging {

  log.debug("CoffeeHouse Open")

  def receive: Receive = {
    case _ => log.info("Coffee Brewing")
  }
}
