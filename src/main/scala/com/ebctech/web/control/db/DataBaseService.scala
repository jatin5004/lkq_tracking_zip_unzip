package com.ebctech.web.control.db

import akka.actor.ActorSystem
import com.ebctech.web.control.actor.{LkqRecordEntity, LkqServiceQuery, Tracking}
import com.ebctech.web.control.service.{DbProvider, LkqHandlers}
import org.slf4j.{Logger, LoggerFactory}
import com.ebctech.web.control.db.entity.LkqRecordTable

import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class DataBaseService {


  var mutableList: mutable.Buffer[String] = ListBuffer()
  var uniqueList: mutable.Buffer[String] = mutableList.toSet.toList.toBuffer


  private  val logger = LoggerFactory.getLogger(this.getClass).asInstanceOf[Logger]

  val db = DbProvider.getInstance()
  def checkPoNumber (orderId: String): Future[Boolean] ={
    val query = LkqServiceQuery.filter(_.order_number_vendor_id === orderId).exists.result
    db.run(query)
  }

  def addOrderRequestToDB(order: LkqRecordEntity): Future[Int] = {

    val insertAction = LkqServiceQuery += order
    val insertResult = db.run(insertAction)
    insertResult

  }


  def getTracking: Future[Int] ={
    implicit val system: ActorSystem = ActorSystem("system")


    val query = LkqServiceQuery.filter(_.vendor_completed_status === "process")
      .map(item => item.tracking_number)
      .distinct
      .result

    val result : Future[Seq[(String)]] = db.run(query)

    result.onComplete {
      case Failure(exception) =>
        logger.info(s"Error ${exception}")
      case Success(value) =>
        uniqueList ++= value.collect{case (value1) => s"$value1 "}.toList.toBuffer

       for(i <- value) {
         var tracking = Tracking(i, "usps")
         LkqHandlers.sendTrackingToAfterShip(tracking)
       }
        uniqueList = ListBuffer.empty[String]
    }
        null
    }


  def getValidId(request: String): Future[Int] = {
    val query = LkqServiceQuery
      .filter(_.tracking_number === request)
      .distinct
      .result

    val resultFuture: Future[Seq[LkqRecordTable#TableElementType]] = db.run(query)

    resultFuture.map { value =>
      if (value.nonEmpty) {
        logger.info(s"Valid Tracking Number: $request")
        1
      } else {
        logger.info(s"Invalid Tracking Number: $request")
        0
      }
    }.recover {
      case exception =>
        logger.error(s"Error while querying the database: ${exception.getMessage}")
        0
    }
  }



  def updateResponse( status: String, trackingNum: String): Future[Int] = {
    val query = LkqServiceQuery
      .filter(_.tracking_number=== trackingNum)
      .map(item => (item.vendor_completed_status, item.tracking_number))
      .update(status, trackingNum)

    db.run(query)
  }


}




