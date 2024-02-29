package com.ebctech.web.control.routes

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.scaladsl.adapter.ClassicActorSystemOps
import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.ebctech.web.control.ApiVersion
import com.ebctech.web.control.actor.{LkqRegistry, TrackingId, TrackingResponse}
import com.ebctech.web.control.actor.LkqRegistry.GetDetails
import com.ebctech.web.control.JsonFormat._

import scala.concurrent.Future
import spray.json.enrichAny

import scala.concurrent.duration.DurationInt



class LkqRoutes(lkqRegistry: ActorRef[LkqRegistry.Query])(implicit system: ActorSystem){

  implicit val scheduler = schedulerFromActorSystem(system.toTyped)
  implicit val timeout: Timeout = Timeout(5.seconds)

def trackOrder(request: TrackingId): Future[TrackingResponse] = lkqRegistry.ask(GetDetails(request,_)).flatten

  val lkqRoutes: Route = pathPrefix(ApiVersion.pathPrefix){

    implicit val orderInfoResponseMarshaller: ToResponseMarshaller[TrackingResponse] =
      Marshaller.opaque { orderInfoResponse =>
        val jsonString = orderInfoResponse.toJson.compactPrint
        HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, jsonString))
      }

    path("tracking") {
      get {
        parameter("trackingId".as[String]) { trackingId =>
          onSuccess(trackOrder(TrackingId(trackingId))) { res =>
            complete(res)

  }

}

    }

    }


  }

}
