package io.kilometer.client

import java.net.URL

import io.kilometer.collector.Collector
import org.json4s._
import org.json4s.native._
import org.scalatest._

class KilometerClientSpec extends FlatSpec {
  implicit val formats = org.json4s.DefaultFormats
  val client = new KilometerClient[(URL, String)](new TestCollector("a", 1, "a1b2c3"))

  "Kilometer client" should "correctly pass through the event case class" in {
    val testAnonEvent = AnonEvent("testEvent", Map("prop1" -> "100", "prop2" -> "200", "prop3" -> "eventProp"))
    val testEventString = compactJson(renderJValue(Extraction.decompose(testAnonEvent)))
    assert(client.addEvent(testAnonEvent)._2 == testEventString)
  }

  it should "correctly increase a users property value" in {
    assert(client.increaseUserProperty("12345", "upprop", 200)._1.getPath == "/users/12345/properties/upprop/increase/200")
  }

  it should "correctly decrease a users property value" in {
    assert(client.decreaseUserProperty("12345", "downprop", 50)._1.getPath == "/users/12345/properties/downprop/decrease/50")
  }
}

class TestCollector(val hostAddress: String, val hostPort: Int, val appId: String) extends Collector[(URL, String)] {
  def post(url: URL, body: JValue): (URL, String) = {
    (url, compactJson(renderJValue(body)))
  }

  def put(url: URL, body: JValue): (URL, String) = {
    (url, compactJson(renderJValue(body)))
  }
}