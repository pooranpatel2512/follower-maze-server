package com.pooranpatel
package eventsource

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.Tcp.{ Register, Connected }
import akka.testkit.{ ImplicitSender, TestProbe }

class EventSourceConnectionListenerSpec extends AsyncActorSpec(ActorSystem("event-source-connection-listener-spec-system")) with ImplicitSender {

  "The EventSourceConnectionListener" must {
    "respond with a new event source connection handler" when {
      "a new connection request arrives on a listening port" in new TestContext {
        // when
        eventSourceConnectionListener ! Connected(new InetSocketAddress(9100), new InetSocketAddress(9101))

        // then
        expectMsgPF(remaining) {
          case Register(_, _, _) =>
        }
      }
    }
  }

  trait TestContext {
    val eventSourceConnectionListener = system.actorOf(EventSourceConnectionListener.props(TestProbe().ref))
  }
}
