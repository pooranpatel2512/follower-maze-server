package com.pooranpatel


import akka.actor.{ ActorSystem, Props }
import eventsource.{ EventDistributor, EventSourceConnectionListener }
import userclient.UserConnectionListener

/**
 * Starting point of an application
 */
object MainApp extends App {

  val system = ActorSystem("follower-maze-server")
  val eventDistributor = system.actorOf(Props[EventDistributor], "event-distributor")
  val userClientConnectionListener = system.actorOf(UserConnectionListener.props(eventDistributor), "user-client-connection-listener")
  val eventSourceConnectionListener = system.actorOf(EventSourceConnectionListener.props(eventDistributor), "event-source-connection-listener")
}
