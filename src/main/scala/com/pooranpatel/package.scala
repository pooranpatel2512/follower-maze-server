package com

package object pooranpatel {

  // Domain model
  type EventsString = String
  type EventString = String
  type UserId = String

  sealed trait Event {
    def value: String
    def sequenceNumber: Int
  }
  case class FollowEvent(value: String, sequenceNumber: Int, followee: UserId, follower: UserId) extends Event
  case class UnFollowEvent(value: String, sequenceNumber: Int, followee: UserId, follower: UserId) extends Event
  case class BroadCastEvent(value: String, sequenceNumber: Int) extends Event
  case class PrivateMsgEvent(value: String, sequenceNumber: Int, from: UserId, to: UserId) extends Event
  case class StatusUpdateEvent(value: String, sequenceNumber: Int, from: UserId) extends Event

  // Exceptions
  case class CorruptedEventReceived(event: EventString) extends RuntimeException {
    override def getMessage: EventString = s"Corrupted event = $event is received from event source"
  }
}
