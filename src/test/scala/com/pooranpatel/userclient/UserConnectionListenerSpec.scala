package com.pooranpatel
package userclient

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.Tcp.{ Register, Connected }
import akka.testkit.{ ImplicitSender, TestProbe }

class UserConnectionListenerSpec extends AsyncActorSpec(ActorSystem("user-connection-listener-spec-system")) with ImplicitSender {

  "The UserConnectionListener" must {
    "respond with a new user connection handler" when {
      "a new connection request arrives on a listening port" in new TestContext {
        // when
        userConnectionListener ! Connected(new InetSocketAddress(9200), new InetSocketAddress(9201))

        // then
        expectMsgPF(remaining) {
          case Register(_, _, _) =>
        }
      }
    }
  }

  trait TestContext {
    val userConnectionListener = system.actorOf(UserConnectionListener.props(TestProbe().ref))
  }
}
