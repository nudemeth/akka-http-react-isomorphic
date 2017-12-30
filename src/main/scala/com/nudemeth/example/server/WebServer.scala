package com.nudemeth.example.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.{Failure, Success}
import scala.concurrent.duration._

object WebServer extends App {
  private val server = WebServer()
  server.start()
}

final case class WebServer() extends ServerRoutes {
  implicit val system: ActorSystem = ActorSystem("akka-http-react-system")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val bindingAddress = "0.0.0.0"
  val bindingPort = 8080
  val shutdownTimeout = 60

  def start(): Unit = {
    log.info(s"Starting server on $bindingAddress:$bindingPort")
    Http().bindAndHandle(routes, bindingAddress, bindingPort)
      .onComplete {
        case Success(binding) =>
          val address = binding.localAddress
          registerShutdownHook(binding)
          log.info(s"Server is listening on ${address.getHostString}:${address.getPort}")
        case Failure(ex) =>
          log.error("Server could not be started", ex)
          stop()
      }
  }

  override def stop(): Unit = {
    log.info("Server is being shut down")
    super.stop()
    system.terminate()
    Await.result(system.whenTerminated, shutdownTimeout seconds)
  }

  private def registerShutdownHook(binding: ServerBinding): Unit = {
    sys.addShutdownHook {
      binding.unbind().onComplete( _ => stop())
    }
  }
}