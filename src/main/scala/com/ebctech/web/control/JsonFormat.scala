package com.ebctech.web.control

import com.ebctech.web.control.actor.{SendTracking, Tracking, TrackingId, TrackingResponse}
import spray.json.DefaultJsonProtocol.{IntJsonFormat, JsValueFormat, StringJsonFormat, jsonFormat1, jsonFormat12, jsonFormat13, jsonFormat14, jsonFormat2, jsonFormat3, jsonFormat4, jsonFormat5, jsonFormat6, listFormat, optionFormat}


trait BasePARequest {
  def accountNum: String
  def client: String
  def userName: String
  def userPass: String
  def action: String
}



object JsonFormat {


  case class FileInfo(
                       path: String,
                       display_name: String,
                       `type`: String,
                       size: Int,
                       created_at: String,
                       mtime: String,
                       provided_mtime: Option[String],
                       crc32: String,
                       md5: String,
                       mime_type: String,
                       region: String,
                       permissions: String
                     )

  case class FileList(list: FileInfo)

  case class FileResponse(
                           status: Int,
                           path: Option[String],

                         )




  implicit val fileInfoFormat = jsonFormat12(FileInfo.apply)
  implicit val fileInFormat = jsonFormat2(FileResponse.apply)
  implicit val fileListFormat = jsonFormat1(FileList)


  implicit val trackingFormat = jsonFormat2(Tracking)
  implicit val trackingIdFormat = jsonFormat1(TrackingId)

  implicit val sendTrackingFormat = jsonFormat1(SendTracking)
  implicit val trackingResponseFormat = jsonFormat3(TrackingResponse)
}
