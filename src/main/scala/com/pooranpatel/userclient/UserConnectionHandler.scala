package com.pooranpatel
package userclient

import akka.actor.{ Props, ActorRef, Actor }
import akka.io.Tcp.{ Write, Received }
import akka.util.ByteString
import settings.Settings
import eventsource.EventDistributor.UserClientConnected

/**
 * Actor responsible for sending events and receiving user id coming from user client
 * @param userConnection user client connection
 * @param eventDistributor reference to event source distributor actor
 */
class UserConnectionHandler(userConnection: ActorRef, eventDistributor: ActorRef) extends Actor {

  val settings = Settings(context.system)

  override def receive: Receive = {
    case Received(data) => eventDistributor ! UserClientConnected(data.utf8String.trim)
    case e: EventString => userConnection ! Write(ByteString(e+settings.eventsSeparator))
  }
}

object UserConnectionHandler {
  def props(userConnection: ActorRef, eventDistributor: ActorRef) = Props(classOf[UserConnectionHandler], userConnection, eventDistributor)
}

