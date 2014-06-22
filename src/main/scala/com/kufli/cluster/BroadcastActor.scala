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

case class Message(message: String)

class BroadcastActor extends Actor with ReceiveLogger with Logging {

  private val cluster = Cluster(context.system)
  private var members = Set.empty[Member]
  private val amqpChannel = AMQPConnection.getConnection().createChannel()
  amqpChannel.exchangeDeclare("evntEx", "direct", true);
  amqpChannel.queueDeclare("evntQ", true, false, false, null)
  amqpChannel.queueBind("evntQ", "evntEx", "public.evnt.scala")
  private val amqpListener = context.actorOf(Props(new AMQPListenerActor("evntQ", amqpChannel)), name = "amqpListener")

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

    case "init" => amqpListener ! "init"

    case mqMessage: MQMessage => log.info(s">>>>>>>><<<<<< Message from [${sender().path.toString}] : [${mqMessage.toString}]")

    case Message(content) =>
      members foreach (pathOf(_) ! content)

    case MemberUp(member) =>
      members += member

    case MemberRemoved(member, previousStatus) =>
      members.find(_.address == member.address) foreach (members -= _)

    case _: MemberEvent => // ignore
  }

  private def pathOf(member: Member) = {
    context.actorSelection(RootActorPath(member.address) / "user" / self.path.name)
  }
}