package com.pooranpatel
package userclient

import java.net.InetSocketAddress
import java.util.UUID

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.Tcp.{ Bind, Connected, Register }
import akka.io.{ IO, Tcp }
import settings.Settings

/**
 * This actor listens on a specified port for incoming user client connections and create a separate handler for each user client
 * @param eventDistributor reference to event source distributor actor
 */
class UserConnectionListener(eventDistributor: ActorRef) extends Actor {

  implicit val system = context.system
  val settings = Settings(context.system)

  IO(Tcp) ! Bind(self, new InetSocketAddress(settings.userClientConnectionPort))

  override def receive: Receive = {
    case Connected(remote, local) =>
      val userConnection = sender()
      val userConnectionHandler = context.actorOf(UserConnectionHandler.props(userConnection, eventDistributor), UUID.randomUUID().toString)
      userConnection ! Register(userConnectionHandler)
  }
}

object UserConnectionListener {
  def props(eventDistributor: ActorRef) = Props(classOf[UserConnectionListener], eventDistributor)
}
