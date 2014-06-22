package com.kufli.cluster

import akka.cluster.{ Member, Cluster }
import akka.cluster.ClusterEvent._
import akka.actor.{ RootActorPath, Actor }
import com.kufli.amqp.AMQPConnection
import akka.actor.Props
import com.kufli.handler.MessageHandlerActor
import com.kufli.amqp.AMQPListenerActor
import com.kufli.amqp.AMQPSenderActor
import com.kufli.log.ReceiveLogger
import com.kufli.log.Logging
import com.kufli.amqp.MQMessage

class BroadcastActor extends IBroadcast with Actor with ReceiveLogger with Logging {

  private val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember])

    self ! "init"
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = logMessage orElse {
    case "init" => startConsuming
    case mqMessage: MQMessage => log.info(s">>>>>>>><<<<<< Message from [${sender().path.toString}] : [${mqMessage.toString}]")
    case MemberUp(member) => addMember(member)
    case MemberRemoved(member, previousStatus) => removeMember(member)
    case _: MemberEvent => // ignore
  }
}

trait IBroadcast { actor: Actor =>

  private var members = Set.empty[Member]
  private val amqpChannel = AMQPConnection.getChannel("evntEx", "evntQ", "public.evnt.scala")

  def startConsuming = {
    val amqpListener = context.actorOf(Props(new AMQPListenerActor("evntQ", amqpChannel)), name = "amqpListener")
    amqpListener ! "init"
  }

  def addMember(member: Member) = members += member
  def removeMember(member: Member) = members.find(_.address == member.address) foreach (members -= _)
  def pathOf(member: Member) = context.actorSelection(RootActorPath(member.address) / "user" / self.path.name)

}