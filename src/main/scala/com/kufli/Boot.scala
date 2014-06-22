package com.kufli

import akka.actor.{ Props, ActorSystem }
import scala.concurrent.duration._
import scala.util.Random
import com.kufli.cluster.Message
import com.kufli.log.Logging
import com.kufli.cluster.BroadcastActor
import akka.actor.{ PoisonPill, ActorSystem, Props }
import akka.contrib.pattern.ClusterSingletonManager

object EvntScala extends App with Logging {
  val system = ActorSystem("evntScala", AkkaConfig.config)

  val clusterSingletonProperties = ClusterSingletonManager.props(
    singletonProps = Props(classOf[BroadcastActor]),
    singletonName = "evntScala-controller",
    terminationMessage = PoisonPill,
    role = None)
  system.actorOf(clusterSingletonProperties, "clusterSingleton")
}