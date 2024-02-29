package com.ebctech.web.control.service

import slick.jdbc.MySQLProfile.api._

object DbProvider {

  private  val db : Database = null

  def getInstance(): Database ={
    if(this.db != null)
      return this.db
      Database.forConfig("database-config.db")

  }
}

//package com.ebctech.web.control.service
//
//import slick.jdbc.PostgresProfile.api._
//import com.typesafe.config.ConfigFactory
//
//object DbProvider {
//  private val config = ConfigFactory.load().getConfig("database-config")
//
//  // Create the database instance using the configuration
//  private val db: Database = Database.forConfig("db", config)
//
//  def getInstance(): Database = db
//}

