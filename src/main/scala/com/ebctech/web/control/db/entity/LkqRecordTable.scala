package com.ebctech.web.control.db.entity

import com.ebctech.web.control.actor.LkqRecordEntity
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

class LkqRecordTable(tags: Tag) extends Table[LkqRecordEntity] (tags, "lkq_tracking"){

  def order_number_vendor_id = column[String]("order_number_vendor_id")

  def tracking_number = column[String]("tracking_number")


  def vendor_completed_status = column[String]("vendor_completed_status")


def * = (order_number_vendor_id,tracking_number, vendor_completed_status) <> (LkqRecordEntity.tupled, LkqRecordEntity.unapply)


}
