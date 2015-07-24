package com.pooranpatel

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{ BeforeAndAfterAll, WordSpecLike }

abstract class AsyncActorSpec(_system: ActorSystem) extends TestKit(_system) with WordSpecLike with BeforeAndAfterAll {
  override protected def afterAll() = {
    system.shutdown()
    system.awaitTermination()
    super.afterAll()
  }
}
