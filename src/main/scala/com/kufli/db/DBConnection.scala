package com.kufli.db

import com.typesafe.config.ConfigFactory
import com.github.mauricio.async.db.Configuration
import com.github.mauricio.async.db.mysql.pool.MySQLConnectionFactory
import com.github.mauricio.async.db.pool._
import com.kufli.common.Logging

object DBConnection {
  private val config = ConfigFactory.load().getConfig("db")

  private val databaseConfiguration = new Configuration(username = config.getString("username"),
    password = Some(config.getString("password")),
    host = config.getString("host"),
    database = Some(config.getString("name")),
    port = config.getInt("port"))

  private lazy val factory = new MySQLConnectionFactory(databaseConfiguration)
  private lazy val pool = new ConnectionPool(factory, PoolConfiguration.Default)

  lazy val messagesRepository = new MessageRepository(pool)

  def close = pool match {
    case x if (x != null) => pool.close
  }
}