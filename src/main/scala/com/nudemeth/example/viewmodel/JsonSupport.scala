package com.nudemeth.example.viewmodel

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val homeFormat: RootJsonFormat[HomeViewModel] = jsonFormat1(HomeViewModel)
  implicit val aboutFormat: RootJsonFormat[AboutViewModel] = jsonFormat1(AboutViewModel)
}
