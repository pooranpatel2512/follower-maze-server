package com.pooranpatel
package eventsource

import java.net.InetSocketAddress
import java.util.UUID

import akka.actor.{ ActorRef, Actor, Props }
import akka.io.Tcp.{ Bind, Connected, Register }
import akka.io.{ IO, Tcp }

import settings.Settings

/**
 * This actor listens on a specified port for incoming event source client connections and create a separate handler for each event source client
 * @param eventDistributor reference to event source distributor actor
 */
class EventSourceConnectionListener(eventDistributor: ActorRef) extends Actor {

  implicit val system = context.system
  val settings = Settings(context.system)

  IO(Tcp) ! Bind(self, new InetSocketAddress(settings.eventSourceConnectionPort))

  override def receive: Receive = {
    case Connected(remote, local) =>
      val connection = sender()
      val eventSourceConnectionHandler = context.actorOf(EventSourceConnectionHandler.props(eventDistributor), "event-source-handler"+UUID.randomUUID().toString)
      connection ! Register(eventSourceConnectionHandler)
  }
}

object EventSourceConnectionListener {
  def props(eventDistributor: ActorRef) = Props(classOf[EventSourceConnectionListener], eventDistributor)
}
