package com.ebctech.web.control.service

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.ebctech.web.control.db.DataBaseService
import com.ebctech.web.control.service.LkqHandlers.dataBaseService

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps



case object request





object LkqScheduler  {


  def apply(): Behavior[request.type ] =
    Behaviors.receive { (context, message)    =>

      Behaviors.same

    }


  case object request

val dataBaseService = new DataBaseService
  implicit val system = ActorSystem("simpleActor")
  val simpleActor = system.actorOf(Props[LkqScheduler])


  class LkqScheduler extends Actor with ActorLogging {
    implicit val system:  ActorSystem = context.system

    import context.dispatcher




    override def preStart(): Unit = {
      scheduleTask()
    }


    def scheduleTask(): Unit = {

      system.scheduler.schedule(0.seconds,24.hours, self, request)

    }


    override def receive: Receive = {
      case request  =>
    LkqHandlers.updateNumIfNeeded



    }






    //    system.scheduler.schedule(5 seconds, 5 seconds) {
    //      self ! request
    //    }
  }

}
