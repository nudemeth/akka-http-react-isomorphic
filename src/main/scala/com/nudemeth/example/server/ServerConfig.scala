package com.nudemeth.example.server

import scala.concurrent.duration.FiniteDuration
import pureconfig.loadConfig

object ServerConfig {
  val config: ServerConfig = loadConfig[ServerConfig]("server") match {
    case Left(_) => throw new Exception("Cannot load configuration file")
    case Right(cfg) => cfg
  }
}

case class ServerConfig(bindingAddress: String,
                        bindingPort: Int,
                        shutdownTimeout: FiniteDuration) {

}
