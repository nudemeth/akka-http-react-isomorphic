package com.nudemeth.example.server

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait ServerRoutes {
  implicit def system: ActorSystem
  def log: LoggingAdapter = Logging(system, this.getClass)

  lazy val route: Route = concat(
    index,
    hello
  )

  private val index: Route = {
    path("") {
      get {
        val content = views.html.index.render()
        log.info(s"Request: route: [default], method:get")
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, content.toString()))
      }
    }
  }

  private val hello: Route = {
    path("hello") {
      get {
        log.info(s"Request: route: hello, method:get")
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }
  }
}
