package com.pooranpatel.eventsource

import akka.actor.ActorSystem
import akka.testkit.{ TestProbe, TestActorRef, ImplicitSender }
import com.pooranpatel.eventsource.EventDistributor.UserClientConnected
import com.pooranpatel.{ UserId, AsyncActorSpec }

class EventDistributorSpec extends AsyncActorSpec(ActorSystem("event-distributor-spec-system")) with ImplicitSender {

  "The EventDistributor" must {
    "update its map of user connections" when {
      "UserClientConnected message is received" in new TestContext {
        // given
        val mockUserConnectionHandler = TestProbe()

        // when
        mockUserConnectionHandler.send(eventDistributor, UserClientConnected(testUserId))

        // then
        assert(eventDistributor.underlyingActor.userConnections.get(testUserId) match {
          case Some(connections) => connections.contains(mockUserConnectionHandler.ref)
          case None => false
        })
      }
    }

    "update its map of userFollowers" when {
      "follow and unfollow events are received as a string in order" in new TestContext {
        // given
        val testUserId1 = "45"
        val testUserId2 = "54"
        val followEventsAsString = "666|F|45|54"
        val unFollowEventsAsString = "75678|U|45|54"
        val mockUserConnectionHandler1 = TestProbe()
        val mockUserConnectionHandler2 = TestProbe()

        // when
        mockUserConnectionHandler1.send(eventDistributor, UserClientConnected(testUserId1))
        mockUserConnectionHandler1.send(eventDistributor, UserClientConnected(testUserId2))

        // then
        eventDistributor ! followEventsAsString
        assert(eventDistributor.underlyingActor.userFollowers.get(testUserId2) match {
          case Some(followers) => followers.contains(testUserId1)
          case None => false
        })
        eventDistributor ! unFollowEventsAsString
        assert(eventDistributor.underlyingActor.userFollowers.get(testUserId2) match {
          case Some(followers) => !followers.contains(testUserId1)
          case None => true
        })
      }
    }

    "distribute a list of events to respective UserConnectionHandler" when {
      "list of events received from EventSourceConnectionHandler " in new TestContext {
        // given
        val testUserId1 = "45"
        val testUserId2 = "54"
        val testUserId3 = "64"
        val mockUserConnectionHandler1 = TestProbe()
        val mockUserConnectionHandler2 = TestProbe()
        val mockUserConnectionHandler3 = TestProbe()
        val eventsAsString = "65678|S|54\n666|F|45|54\n75879|S|54\n75678|U|45|54\n542532|B\n43|P|45|64"

        // when
        mockUserConnectionHandler1.send(eventDistributor, UserClientConnected(testUserId1))
        mockUserConnectionHandler2.send(eventDistributor, UserClientConnected(testUserId2))
        mockUserConnectionHandler3.send(eventDistributor, UserClientConnected(testUserId3))
        eventDistributor ! eventsAsString

        // then
        mockUserConnectionHandler3.expectMsg("43|P|45|64")
        mockUserConnectionHandler2.expectMsg("666|F|45|54")
        mockUserConnectionHandler1.expectMsg("65678|S|54")
        mockUserConnectionHandler1.expectMsg("542532|B")
        mockUserConnectionHandler2.expectMsg("542532|B")
        mockUserConnectionHandler3.expectMsg("542532|B")
      }
    }
  }

  trait TestContext {
    val testUserId: UserId = "test-user-id"
    val eventDistributor = TestActorRef[EventDistributor]
  }
}
