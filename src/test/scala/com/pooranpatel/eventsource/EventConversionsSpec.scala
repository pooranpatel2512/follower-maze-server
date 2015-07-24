package com.pooranpatel
package eventsource

import akka.actor.ActorSystem
import settings.Settings

import scala.util.Failure

class EventConversionsSpec extends AsyncActorSpec(ActorSystem("event-conversions-spec-system")) {

  val settings = Settings(system)

  "The EventConversions" must {
    "convert valid follow event string to FollowEvent in domain model" in new EventConversions {
      val eventString = "666|F|60|50"
      val event = eventStringToEvent(eventString, settings.eventFieldsSeparator)
      assert(event.get == FollowEvent(eventString, 666, "50", "60"))
    }

    "convert valid unfollow event string to UnFollowEvent in domain model" in new EventConversions {
      val eventString = "12|U|4|67"
      val event = eventStringToEvent(eventString, settings.eventFieldsSeparator)
      assert(event.get == UnFollowEvent(eventString, 12, "67", "4"))
    }

    "convert valid broadcast event string to BroadCastEvent in domain model" in new EventConversions {
      val eventString = "542532|B"
      val testEvent = eventStringToEvent(eventString, settings.eventFieldsSeparator)
      assert(testEvent.get == BroadCastEvent(eventString, 542532))
    }

    "convert valid private message event string to PrivateMsgEvent in domain model" in new EventConversions {
      val eventString = "43|P|32|56"
      val event = eventStringToEvent(eventString, settings.eventFieldsSeparator)
      assert(event.get == PrivateMsgEvent(eventString, 43, "32", "56"))
    }

    "convert valid status update event string to StatusUpdateEvent in domain model" in new EventConversions {
      val eventString = "634|S|32"
      val event = eventStringToEvent(eventString, settings.eventFieldsSeparator)
      assert(event.get == StatusUpdateEvent(eventString, 634, "32"))
    }

    "fail to convert invalid event string to Event in domain model" in new EventConversions {
      val eventString1 = "234|"
      val event1 = eventStringToEvent(eventString1, settings.eventFieldsSeparator)
      assert(event1 match {
        case Failure(ex: CorruptedEventReceived) => true
        case _ => false
      })

      val eventString2 = "234DF|U|23"
      val event2 = eventStringToEvent(eventString2, settings.eventFieldsSeparator)
      assert(event2 match {
        case Failure(ex: CorruptedEventReceived) => true
        case _ => false
      })
    }

    "convert all valid events string to Events in domain model" in new EventConversions {
      val events1AsString = "65678|S|54\n666|F|45|54\n542532|B\n43|P|45|64"
      val events1 = eventsStringToEvents(events1AsString, settings.eventsSeparator, settings.eventFieldsSeparator)
      assert(events1 sameElements Array(
        StatusUpdateEvent("65678|S|54", 65678, "54"),
        FollowEvent("666|F|45|54", 666, "54", "45"),
        BroadCastEvent("542532|B", 542532),
        PrivateMsgEvent("43|P|45|64", 43, "45", "64")
      ))

      val events2AsString = "65678|S\n666|F|45|54"
      val events2 = eventsStringToEvents(events2AsString, settings.eventsSeparator, settings.eventFieldsSeparator)
      assert(events2 sameElements Array(
        FollowEvent("666|F|45|54", 666, "54", "45")
      ))
    }
  }
}
