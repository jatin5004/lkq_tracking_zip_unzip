package com.ebctech.web.control.service


import com.ebctech.web.control.service.UnzipFiles._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, StatusCodes, headers}
import akka.http.scaladsl.unmarshalling.Unmarshal
import ch.qos.logback.classic.Logger
import com.ebctech.web.control.actor.{LkqRecordEntity, SendTracking, Tracking, TrackingId, TrackingResponse}
import com.ebctech.web.control.db.DataBaseService
import org.slf4j.LoggerFactory
import spray.json.{DefaultJsonProtocol, JsObject}

import java.net.URL
import spray.json._

import java.io.{BufferedInputStream, File, FileOutputStream, IOException}
import java.nio.file.{Files, Paths}
import java.time.{LocalDate, ZonedDateTime}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.time.format.DateTimeFormatter
import scala.Console.println
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.tools.nsc.tasty.SafeEq
import scala.util.{Failure, Success}
import com.ebctech.web.control.JsonFormat._



object LkqHandlers extends DefaultJsonProtocol with SprayJsonSupport {
  private final val logger = LoggerFactory.getLogger(this.getClass).asInstanceOf[Logger]

private val dataBaseService = new DataBaseService()

  var currentDate = LocalDate.now()
  val oneDayAgo = currentDate.minusDays(2)


  var mutableList: mutable.Buffer[String] = ListBuffer()
  var uniqueList: mutable.Buffer[String] = mutableList.toSet.toList.toBuffer


  def updateNumIfNeeded(implicit system: ActorSystem): FileResponse = {


    Http().singleRequest(
      HttpRequest(HttpMethods.GET, uri = s"")
        .withHeaders(headers.Authorization(headers.BasicHttpCredentials("", "x")))
    ).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String].map { body =>
            val json = body.parseJson
            json match {
              case JsArray(elements) =>
                val createdTimes = elements.map {
                  case JsObject(fields) => fields.get("created_at") match {
                    case Some(JsString(createdTime)) => createdTime
                    case _ => ""
                  }
                  case _ => ""
                }
                println(createdTimes)
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")



                for(i <- createdTimes) {
                  ZonedDateTime.parse(i).format(dateFormatter)

                                  if (ZonedDateTime.parse(i).format(dateFormatter).toString === oneDayAgo.toString) {

                                    val displayName = elements.collect {
                                      case JsObject(fields) if fields.get("created_at").contains(JsString(i)) =>
                                        fields.get("display_name") match {
                                          case Some(JsString(displayName)) => displayName
                                          case _ => ""
                                        }
                                    }
                                    uniqueList ++= displayName
                                  }
                }
                println(uniqueList)
                for(i <- uniqueList){
                  lkqDownload(i)
                }

            }
uniqueList = ListBuffer.empty[String]

          }

        case _ =>
          println(s"Request failed with status code ${response.status}")
          response.discardEntityBytes()
          Future.successful((0))
      }

  }
    null
  }




    def lkqDownload(req: String)(implicit system: ActorSystem): FileResponse = {

    case class FileInfo(display_name: String, download_uri: String)

    implicit val fileInfoFormat: RootJsonFormat[FileInfo] = jsonFormat2(FileInfo)


       Http().singleRequest(
        HttpRequest(HttpMethods.GET, uri = s"uri/${req}")
          .withHeaders(headers.Authorization(headers.BasicHttpCredentials("", "x")))
      ).flatMap { response =>
         Unmarshal(response.entity).to[FileInfo].map { res =>

//                      logger.info(s"${res}")

try{

  val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  val folderName =   (s"C:\\Users\\kj911\\OneDrive\\Desktop\\unzip_files\\Zip_Files\\${formattedDate}")

  val newFolder = new File(folderName)
  if (!newFolder.exists()) newFolder.mkdir()


           val url = new URL(res.download_uri)
           logger.info(url.toString)
  val targetPath = Paths.get(s"C:\\Users\\kj911\\OneDrive\\Desktop\\unzip_files\\Zip_Files\\${currentDate}\\${res.display_name}")

  val displayName = res.display_name
           downloadFile(url.toString, targetPath.toString, displayName)
           val zipFilePath = s"C:\\Users\\kj911\\OneDrive\\Desktop\\unzip_files\\Zip_Files\\${currentDate}\\${displayName}"

           val destDirectory = s"C:\\Users\\kj911\\OneDrive\\Desktop\\unzip_files\\unzip_files\\${currentDate}"
           logger.info(zipFilePath)
           unzipFiles(zipFilePath, destDirectory)
           readXmlFile
  dataBaseService.getTracking

  Future.successful(FileResponse(200, Some(res.display_name)))
         } catch {

  case e: Exception =>
    logger.info("download Error with msg: -  "+ e.getMessage)
}
         }


       }
null
  }


  def downloadFile(url: String, targetPath: String, name: String): Unit = {
    val connection = new URL(url).openConnection()
    val inputStream = new BufferedInputStream(connection.getInputStream)
    val outputStream = new FileOutputStream(targetPath)
    val buffer = new Array[Byte](1024)
    var bytesRead = inputStream.read(buffer)


    try {
      while (bytesRead != -1) {
        outputStream.write(buffer, 0, bytesRead)
        bytesRead = inputStream.read(buffer)
      }
    } catch {
      case e: IOException => println(s"Error downloading file: ${e.getMessage}")
    } finally {
      inputStream.close()
      outputStream.close()
    }

    println(s"File downloaded to: $targetPath")

}


  def  readXmlFile(implicit system: ActorSystem): Unit ={


    val path = s"C:\\Users\\kj911\\OneDrive\\Desktop\\unzip_files\\unzip_files\\${currentDate}"
    val files = Files.list(Paths.get(path)).iterator().asScala.toList

    val xmlFiles = files.filter(_.toString.toUpperCase.endsWith(".xml"))

    files.foreach { filePath =>
      val xmlContent = scala.xml.XML.loadFile(filePath.toFile).toString

      val jsonString = extractOrderInfo(xmlContent)


      println(s"Processing XML file: ${filePath.getFileName}")
    }}



  import scala.xml.XML
def extractOrderInfo(xmlString: String): (String, String) = {
  try {
    val xml = XML.loadString(xmlString)

    val poNumber = (xml \ "PONumber").text
    val trackingNumber = (xml \\ "TrackingNumber").text



    val saveData = LkqRecordEntity(poNumber, trackingNumber,  "process")
    dataBaseService.addOrderRequestToDB(saveData).onComplete {
      case Success(value) =>
        logger.info("Data is saved In DataBase")
      case Failure(exception) =>
        logger.info(s"Error while saving data in DataBase: - ${exception.getMessage}")
    }

    (poNumber, trackingNumber)
  }



  catch {
    case e: Exception =>
      println(s"Error reading XML data: ${e.getMessage}")
      ("", "")
  }
}



     def sendTrackingToAfterShip(tracking: Tracking)(implicit system: ActorSystem): Future[TrackingResponse] ={
     var requestJson = getTrackingPayload(tracking)
//     logger.info(requestJson.toJson.prettyPrint)

       Http().singleRequest(HttpRequest(HttpMethods.POST, uri = "uri", entity = HttpEntity(ContentTypes.`application/json`, requestJson.toJson.prettyPrint)).withHeaders(headers.RawHeader("as-api-key", "asat_79a98ce5e61c46c79b523a7844d5dd33"))).flatMap { response =>
       if (response.status.intValue == 201 || response.status.intValue == 200) {
      Unmarshal(response.entity).to[String].flatMap { res =>

        println("Tracking send Successfully" + tracking)
         updateTracking("Completed",tracking.tracking_number)
        Future.successful(TrackingResponse(200, res.parseJson, request.toString))
      }
    } else {
      Future.successful(TrackingResponse(400, JsString(response.toString), request.toString))
    }
  }

}

  def getTrackingPayload(request: Tracking): SendTracking = {
    SendTracking(request)
  }


  def trackOrder(request : TrackingId)(implicit system: ActorSystem): Future[TrackingResponse] ={


    val validIdFuture = dataBaseService.getValidId(request.trackingNum)

    validIdFuture.onComplete {
      case Success(value) =>

        logger.info(s"value:  ${value.toString}")
        Future.successful(1)

      case Failure(exception) =>
        logger.error(s"${exception.getMessage}")
        Future.successful(0)
    }




    Http().singleRequest(HttpRequest(HttpMethods.GET, uri = s"uri/${request.trackingNum}").withHeaders(headers.RawHeader("as-api-key", "asat_79a98ce5e61c46c79b523a7844d5dd33"))).flatMap { response =>


      if (response.status.intValue == 200) {
        Unmarshal(response.entity).to[String].flatMap { res =>
          Future.successful(TrackingResponse(200, res.parseJson, request.trackingNum))
        }

      } else {
        response.discardEntityBytes()
        Future.successful(TrackingResponse(400, JsString(response.toString), request.trackingNum))
      }
    }
  }

  def updateTracking( status:String, trackingNum: String): Future[Unit] = {
    val query = dataBaseService.updateResponse(status, trackingNum)

    query.onComplete {
      case Failure(exception) =>
        logger.error(s"Error updating orderId: $trackingNum - ${exception.getMessage}")
        throw exception
      case Success(result) =>
        if (result > 0) {
          logger.info(s"Update successful for orderId: ${trackingNum}")
        } else {
          logger.info(s"Update failed for orderId: ${trackingNum}")
        }

    }
    Future.successful(0)
  }



  }