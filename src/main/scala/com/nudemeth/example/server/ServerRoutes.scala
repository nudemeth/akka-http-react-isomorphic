package com.nudemeth.example.server

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.nudemeth.example.viewmodel._
import com.nudemeth.example.engine._
import spray.json._

trait ServerRoutes extends JsonSupport {
  implicit def system: ActorSystem
  def log: LoggingAdapter = Logging(system, this.getClass)

  lazy val routes: Route = concat(
    pageRoutes,
    resourceRoutes,
    dataRoutes,
  )

  private lazy val pageRoutes: Route = concat(
    home,
    about,
  )

  private lazy val resourceRoutes: Route = concat(
    js,
  )

  private lazy val dataRoutes: Route = concat(
    dataHome,
    dataAbout,
  )

  private lazy val renderer: JavaScriptEngine = J2V8Engine.instance.registerScripts(
    Seq(
      ScriptURL(getClass.getResource("/webapp/js/polyfill/j2v8-polyfill.js")),
      ScriptURL(getClass.getResource("/webapp/js/bundle.js")),
      ScriptText("var frontend = new com.nudemeth.example.web.Frontend();")
    )
  ).build

  private val home: Route = {
    pathEndOrSingleSlash {
      get {
        val model = HomeViewModel("This is Home page").toJson.compactPrint
        val content = renderer.invokeMethod[String]("frontend", "renderServer", "/", model)
        val html = views.html.index.render(content, model).toString()
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
      }
    }
  }

  private val about: Route = {
    path("about") {
      pathEndOrSingleSlash {
        get {
          val model = AboutViewModel("About page").toJson.compactPrint
          val content = renderer.invokeMethod[String]("frontend", "renderServer", "/about", model)
          val html = views.html.index.render(content, model).toString()
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
        }
      }
    }
  }

  private val dataHome: Route = {
    path("data" / "home") {
      pathEndOrSingleSlash {
        get {
          val model = HomeViewModel("This is Home page").toJson.compactPrint
          complete(HttpEntity(ContentTypes.`application/json`, model))
        }
      }
    }
  }

  private val dataAbout: Route = {
    path("data" / "about") {
      pathEndOrSingleSlash {
        get {
          val model = AboutViewModel("About page").toJson.compactPrint
          complete(HttpEntity(ContentTypes.`application/json`, model))
        }
      }
    }
  }

  private val js: Route = {
    get {
      pathPrefix("js" / Segment) { file =>
        val js = scala.io.Source.fromURL(getClass.getResource(s"/webapp/js/$file"))("UTF-8").mkString
        complete(HttpEntity(MediaTypes.`application/javascript` withCharset HttpCharsets.`UTF-8`, js))
      }
    }
  }

  def stop(): Unit = {
    renderer.destroy
  }
}
