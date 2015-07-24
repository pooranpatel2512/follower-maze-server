package com.pooranpatel
package eventsource

import akka.actor.{ ActorRef, Actor }
import eventsource.EventDistributor.UserClientConnected
import settings.Settings

import scala.collection.mutable

/**
 * This actor distributes the events received from [[EventSourceConnectionHandler]] to respective [[userclient.UserConnectionHandler]]
 */
class EventDistributor extends Actor with EventConversions {

  val userConnections = mutable.Map.empty[UserId, ActorRef]

  val userFollowers = mutable.Map.empty[UserId, mutable.Set[ActorRef]]

  val settings = Settings(context.system)

  override def receive: Receive = {
    case es: EventsString =>
      val sortedEvents = sortEventsAscending(eventsStringToEvents(es, settings.eventsSeparator, settings.eventFieldsSeparator))
      sortedEvents foreach ( e => handleEvent(e) )
    case UserClientConnected(id) => userConnections += (id -> sender())
  }

  private def sortEventsAscending(events: Array[Event]) = events.sortWith(_.sequenceNumber < _.sequenceNumber)

  private def handleEvent(event: Event) = {
    event match {
      case FollowEvent(es, _, followee, follower) => follow(followee, follower, es)
      case UnFollowEvent(es, _, followee, follower) => unFollow(followee, follower)
      case BroadCastEvent(es, _) => userConnections.values.foreach(uch => uch ! es)
      case PrivateMsgEvent(es, _, from, to) => userConnections.get(to).foreach( uch => uch ! es )
      case StatusUpdateEvent(es, _, from) => userFollowers.get(from).foreach( ucs => ucs.foreach( uc => uc ! es))
      case _ => // ignore
    }
  }

  private def follow(followee: UserId, follower: UserId, eventsString: EventsString): Unit = {
    val followers = userFollowers.getOrElseUpdate(followee, mutable.Set.empty[ActorRef])
    userConnections.get(followee) foreach( uc => uc ! eventsString )
    userConnections.get(follower) foreach( uc => followers += uc)
  }

  private def unFollow(followee: UserId, follower: UserId): Unit = {
    for {
      followers <- userFollowers.get(followee)
      userConnectionHandler <- userConnections.get(follower)
    } {
      followers -= userConnectionHandler
    }
  }
}

object EventDistributor {

  case class UserClientConnected(id: UserId)
}
