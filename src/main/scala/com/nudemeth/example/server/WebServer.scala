package com.nudemeth.example.server

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

object WebServer extends App {
  private val server = WebServer()
  server.start(ServerRoute.route)
  StdIn.readLine() // let it run until user presses return
  server.stop()
}

final case class WebServer() {
  private implicit val system: ActorSystem = ActorSystem("akka-http-react-system")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  private implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private var server: Future[Http.ServerBinding] = _
  private lazy val log = Logging(system, classOf[WebServer])

  def start(route: Route): Unit = {
    server = Http().bindAndHandle(route, "localhost", 8080)
    log.info(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  }

  def stop(): Unit = {
    server
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}