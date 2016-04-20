package io.kilometer.client

import java.net.URL
import io.kilometer.client.response.ResponseLogger
import io.kilometer.collector._
import org.json4s.Extraction
import org.json4s.JsonAST.JObject
import scala.util.Properties

object KilometerClient extends FinagleBackedCollector with ResponseLogger {
  implicit val formats = org.json4s.DefaultFormats

  private val apiPort = 443
  private[this] val appID = Properties.envOrElse("KILOMETER_APPID", "")
  private[this] val apiURL = Properties.envOrElse("KILOMETER_URL", "")
  private val httpClient = underlying.client(apiURL, apiPort)

  private def urlBuilder(path: String) = new URL("https", apiURL, apiPort, path)

  private def changeUserProperty(user: String, property: String, value: Long, stateChange: String = "increase") = {
    underlying.post(httpClient, urlBuilder(s"/users/$user/properties/$property/$stateChange/$value"), appID, JObject())
  }

  def addEvent(event: KilometerEvent) = {
    underlying.post(httpClient, urlBuilder("/events"), appID, Extraction.decompose(event))
  }

  def updateUserProperties(user: String, properties: Map[String, String]) = {
    underlying.put(httpClient, urlBuilder(s"/users/$user/properties"), appID, Extraction.decompose(properties))
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



