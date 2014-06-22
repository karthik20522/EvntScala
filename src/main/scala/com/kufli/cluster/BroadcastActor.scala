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
import scala.concurrent.duration.Duration

case object MasterIdList

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
    case mqMessage: MQMessage => distributeWork(mqMessage)
    case MemberUp(member) => addMember(member)
    case MemberRemoved(member, previousStatus) => removeMember(member)
    case UnreachableMember(member) => //do something
    case _: MemberEvent => // ignore
  }
}

trait IBroadcast { actor: Actor =>

  private var members = Set.empty[Member]
  private var masterIds: Map[String, Member] = Map()

  private val circular = Iterator.continually(members).flatten
  private val amqpChannel = AMQPConnection.getChannel("evntEx", "evntQ", "public.evnt.scala")

  /*
   * Consume from RabbitMQ
   */
  def startConsuming = {
    val amqpListener = context.actorOf(Props(new AMQPListenerActor("evntQ", amqpChannel)), name = "amqpListener")
    amqpListener ! "init"
  }

  /*
   * Add the new node that joined the cluster
   */
  def addMember(member: Member) = members += member

  /*
   * Remove the node and the associated MasterId lookup
   */
  def removeMember(member: Member) = {
    members.find(_.address == member.address) foreach (members -= _)
    //masterIds -= masterIds.find(_._2 == member).head._1
  }

  /*
   * Figure out if the any child node is processing the masterId
   * 1. If so, then send the message to that particular node
   * 2. If not, then send the message to a random node to start the processing
   */
  def distributeWork(mqMessage: MQMessage) = {
    val masterId = mqMessage.messageProperties.headers.find(_._1 == "masterId").head._2.toString()

    //TODO: This is incomplete - Pending checks 1 & 2

    val node = pathOf(getChildNode)
    node ! (masterId, mqMessage)
  }

  /*
   * Circular queue - Get the next child node in the list
   */
  def getChildNode() = circular.take(1).next

  /*
   * Get the remote actor selection ref
   */
  def pathOf(member: Member) = context.actorSelection(RootActorPath(member.address) / "user" / "messageHandler")
}