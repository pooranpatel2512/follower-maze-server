package com.pooranpatel
package eventsource

import akka.actor.{ Props, ActorRef, Actor }
import akka.io.Tcp.Received

/**
 * Actor responsible for receiving events coming from event source client and forwarding them to event distributor
 * @param eventDistributor reference to event source distributor actor
 */
class EventSourceConnectionHandler(eventDistributor: ActorRef) extends Actor {

  override def receive: Receive = {
    case Received(data) => eventDistributor ! data.utf8String.trim
  }
}

object EventSourceConnectionHandler {
  def props(eventDistributor: ActorRef) = Props(classOf[EventSourceConnectionHandler], eventDistributor)
}
