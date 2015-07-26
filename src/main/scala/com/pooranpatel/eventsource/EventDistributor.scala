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

  val userConnections = mutable.Map.empty[UserId, mutable.ListBuffer[ActorRef]]

  val userFollowers = mutable.Map.empty[UserId, mutable.Set[UserId]]

  val settings = Settings(context.system)

  override def receive: Receive = {
    case es: EventsString =>
      val sortedEvents = sortEventsAscending(eventsStringToEvents(es, settings.eventsSeparator, settings.eventFieldsSeparator))
      sortedEvents foreach ( e => handleEvent(e) )
    case UserClientConnected(id) =>
      val connections = userConnections.getOrElseUpdate(id, mutable.ListBuffer.empty[ActorRef])
      sender() +=: connections
  }

  private def sortEventsAscending(events: Array[Event]) = events.sortWith(_.sequenceNumber < _.sequenceNumber)

  private def handleEvent(event: Event) = {
    event match {
      case FollowEvent(es, _, followee, follower) => follow(followee, follower, es)
      case UnFollowEvent(es, _, followee, follower) => unfollow(followee, follower)
      case BroadCastEvent(es, _) => broadCastMessage(es)
      case PrivateMsgEvent(es, _, from, to) => sendPrivateMessage(from, to, es)
      case StatusUpdateEvent(es, _, from) => sendUpdates(from, es)
      case _ => // ignore
    }
  }

  /**
   * Add follower to list of followers for given followee and send follow event to followee
   */
  private def follow(followee: UserId, follower: UserId, eventString: EventString): Unit = {
    val followers = userFollowers.getOrElseUpdate(followee, mutable.Set.empty[UserId])
    followers += follower
    for {
      connections <- userConnections.get(followee)
      uc <- connections
    } {
      uc ! eventString
    }
  }

  /**
   * Remove follower from list of followers for given followee
   */
  private def unfollow(followee: UserId, follower: UserId): Unit = {
    userFollowers.get(followee) foreach (followers => followers -= follower)
  }

  /**
   * Broadcast event to all user clients
   */
  private def broadCastMessage(eventString: EventString): Unit = {
    for {
      connections <- userConnections.values
      uc <- connections
    } {
      uc ! eventString
    }
  }

  /**
   * Send private message event to user client identified by to user id
   */
  private def sendPrivateMessage(from: UserId, to: UserId, eventString: EventString): Unit = {
    for {
      connections <- userConnections.get(to)
      uc <- connections
    } {
      uc ! eventString
    }
  }

  /**
   * Send event as a update message to all the followers of user id identified by from
   */
  private def sendUpdates(from: UserId, eventString: EventsString): Unit = {
    for {
      followers <- userFollowers.get(from)
      follower <- followers
      userConnections <- userConnections.get(follower)
      uc <- userConnections
    } {
      uc ! eventString
    }
  }
}

object EventDistributor {
  case class UserClientConnected(id: UserId)
}
