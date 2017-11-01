package com.nudemeth.example.server

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object ServerRoute {
  lazy val route: Route = concat(
    index,
    hello
  )

  private val index: Route = {
    path("") {
      get {
        val content = views.html.index.render()
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, content.toString()))
      }
    }
  }

  private val hello: Route = {
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }
  }
}
