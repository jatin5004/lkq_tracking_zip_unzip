package com.ebctech.web.control.db

import akka.util.Timeout
import akka.util.Timeout
import com.typesafe.config.Config
import slick.jdbc.MySQLProfile.api._



trait DBLKQ {

def config : Config

  implicit val timeout: Timeout = Timeout.create(config.getDuration("taps.routes.ask-timeout"))

final val SECURED_REALM = "secured"

private val db = Database.forURL(

  driver = config.getString("sql.driver"),
    url = config.getString("sql.url"),
  user = config.getString("sql.username"),
  password = config.getString("sql.password")

  )




}
