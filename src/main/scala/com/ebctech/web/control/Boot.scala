package com.ebctech.web.control

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.ebctech.web.control.actor.LkqRegistry
import com.ebctech.web.control.routes.LkqRoutes
import com.ebctech.web.control.service.LkqScheduler

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

object Boot {

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit ={

    val host = system.settings.config.getString("taps.host")
    val port = system.settings.config.getInt("taps.port")

    val bindingFuture = Http().newServerAt(host,port).bind(routes)
    bindingFuture.onComplete{
      case scala.util.Success(binding)=>
        val address= binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)

      case scala.util.Failure(exception) =>
        system.log.error("Failed to bind Http endpoint, terminating system", exception)
        system.terminate()
    }

  }

  def main(args: Array[String]): Unit ={
    val rootBehavior = Behaviors.setup[Any] { context =>
      val lkqRegistryActor = context.spawn(LkqRegistry(context.system.classicSystem), "lkqRegistryActor")
      context.watch(lkqRegistryActor)

      val lkqSchedulerActor = context.spawn(LkqScheduler(), "lkqSchedulerActor")
      context.watch(lkqSchedulerActor)
      lkqSchedulerActor ! LkqScheduler.request

      val lkqRoute = new LkqRoutes(lkqRegistryActor)(context.system.classicSystem)
      startHttpServer(lkqRoute.lkqRoutes)(context.system)
Behaviors.empty

    }
    ActorSystem[Any](rootBehavior, "TapsHttpServer")
  }
}
