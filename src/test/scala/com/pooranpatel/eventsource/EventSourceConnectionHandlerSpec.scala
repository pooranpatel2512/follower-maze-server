package com.pooranpatel.eventsource

import akka.actor.ActorSystem
import akka.io.Tcp.Received
import akka.testkit.TestProbe
import akka.util.ByteString
import com.pooranpatel.AsyncActorSpec

class EventSourceConnectionHandlerSpec extends AsyncActorSpec(ActorSystem("event-source-connection-handler-spec-system")) {
  "The EventSourceConnectionHandler" must {
    "forward events string to event distributor" in {
      // given
      val dataString = "11243|S|34\n12|P|45|78"
      val data = ByteString("11243|S|34\n12|P|45|78  ")
      val mockEventDistributor = TestProbe()
      val eventSourceConnectionHandler = system.actorOf(EventSourceConnectionHandler.props(mockEventDistributor.ref))

      // when
      eventSourceConnectionHandler ! Received(data)

      // then
      mockEventDistributor.expectMsg(dataString)
    }
  }
}
