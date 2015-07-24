package com.pooranpatel.userclient

import akka.actor.ActorSystem
import akka.io.Tcp.{ Write, Received }
import akka.testkit.TestProbe
import akka.util.ByteString
import com.pooranpatel.AsyncActorSpec
import com.pooranpatel.eventsource.EventDistributor.UserClientConnected

class UserConnectionHandlerSpec extends AsyncActorSpec(ActorSystem("user-connection-handler-spec-system")) {

  "The UserConnectionHandler" must {
    "send UserClientConnected message to event distributor" when {
      "user id is received from user client" in new TestContext {
        // given
        val testUserId = "234"

        // when
        userConnectionHandler ! Received(ByteString(testUserId))

        // then
        mockEventDistributor.expectMsg(UserClientConnected(testUserId))
      }
    }
    "forward event string to user client" when {
      "event string is received" in new TestContext {
        // given
        val eventString = "123|P|34|78"

        // when
        userConnectionHandler ! eventString

        // then
        mockUserConnection.expectMsg(Write(ByteString(eventString+"\n")))
      }
    }
  }

  trait TestContext {
    val mockUserConnection = TestProbe()
    val mockEventDistributor = TestProbe()
    val userConnectionHandler = system.actorOf(UserConnectionHandler.props(mockUserConnection.ref, mockEventDistributor.ref))
  }
}
