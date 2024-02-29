package com.ebctech.web.control.actor

import akka.actor.ActorSystem
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.ActorMaterializer
import com.ebctech.web.control.db.entity.LkqRecordTable
import com.ebctech.web.control.service.LkqHandlers.{lkqDownload, trackOrder, updateNumIfNeeded}
import slick.lifted.TableQuery
import spray.json.JsValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



case class File(filePath: String)

case class FileResponse(response: String)

case class LkqRecordEntity(order_number_vendor_id: String, tracking_number: String, vendor_completed_status: String)

final case class Tracking(tracking_number: String, slug: String)

final case class TrackingId(trackingNum: String)

final case class SendTracking(tracking: Tracking)

final case class TrackingResponse(status: Int, response: JsValue, orderTracking: String)


object LkqServiceQuery extends TableQuery(new LkqRecordTable(_))


object LkqRegistry {

  case object FileDownloadQuery extends Query

  case class GetDetails(request: TrackingId, replyTo: ActorRef[Future[TrackingResponse]]) extends Query
  sealed trait Query

  def apply(implicit system: ActorSystem): Behavior[Query] =
    Behaviors.receiveMessage {
      case FileDownloadQuery =>
        updateNumIfNeeded
        println("done")
        Behaviors.same

      case GetDetails(request, replyTo)=>
          replyTo ! trackOrder(request)
        Behaviors.same



    }

}
