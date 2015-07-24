package com.pooranpatel.eventsource

import com.pooranpatel._

import scala.util.{ Try, Failure, Success }

/**
 * This trait provides a support for event string to Domain event conversions
 */
trait EventConversions {

  /**
   * This method converts an events as a string to correct array of domain events
   * @param eventsString events as a string
   * @param eventsSeparator separator for events
   * @param eventFieldsSeparator separator for event fields
   * @return array of domain events
   */
  protected def eventsStringToEvents(eventsString: EventsString, eventsSeparator: String, eventFieldsSeparator: String): Array[Event] = {
    eventsString.split(eventsSeparator) flatMap { es =>
      val event = eventStringToEvent(es, eventFieldsSeparator)
      event match {
        case Success(e) => Some(e)
        case Failure(ex) => None
      }
    }
  }

  /**
   * Converts a event as a string to valid domain event
   * @param eventString event as a string
   * @param eventFieldsSeparator separator for event fields
   * @return valid domain event or exception in case of failure
   */
  protected def eventStringToEvent(eventString: EventString, eventFieldsSeparator: String): Try[Event] = {
    val fields = eventString.split(eventFieldsSeparator)
    Try {
      try {
        fields(1) match {
          case "F" => FollowEvent(eventString, fields(0).toInt, fields(3), fields(2))
          case "U" => UnFollowEvent(eventString, fields(0).toInt, fields(3), fields(2))
          case "B" => BroadCastEvent(eventString, fields(0).toInt)
          case "P" => PrivateMsgEvent(eventString, fields(0).toInt, fields(2), fields(3))
          case "S" => StatusUpdateEvent(eventString, fields(0).toInt, fields(2))
        }
      } catch {
        case ex: ArrayIndexOutOfBoundsException => throw CorruptedEventReceived(eventString)
        case ex: NumberFormatException => throw CorruptedEventReceived(eventString)
        case ex: Throwable => throw ex
      }
    }
  }
}