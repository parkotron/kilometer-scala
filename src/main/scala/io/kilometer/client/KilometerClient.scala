package io.kilometer.client

import java.net.URL
import io.kilometer.client.response.ResponseLogger
import io.kilometer.collector._
import org.json4s.Extraction
import org.json4s.JsonAST.JObject

class KilometerClient[T](collector: Collector[T]) extends ResponseLogger {
  implicit val formats = org.json4s.DefaultFormats
  private def urlBuilder(path: String) = new URL("https", collector.hostAddress, collector.hostPort, path)

  private def changeUserProperty(user: String, property: String, value: Long, stateChange: String = "increase") = {
    collector.post(urlBuilder(s"/users/$user/properties/$property/$stateChange/$value"), JObject())
  }

  def addEvent(event: KilometerEvent) = {
    collector.post(urlBuilder("/events"), Extraction.decompose(event))
  }

  def updateUserProperties(user: String, properties: Map[String, String]) = {
    collector.put(urlBuilder(s"/users/$user/properties"), Extraction.decompose(properties))
  }

  def increaseUserProperty(user: String, property: String, value: Long) = {
    changeUserProperty(user, property, value)
  }

  def decreaseUserProperty(user: String, property: String, value: Long) = {
    changeUserProperty(user, property, value, "decrease")
  }
}

case class AnonEvent(event_name: String, event_properties: Map[String, String]) extends KilometerEvent
case class IdentifiedEvent(user_id: String, event_name: String, event_properties: Map[String, String]) extends IdentifiedKilometerEvent



