package io.kilometer.collector

import java.net.URL
import org.json4s._

trait Collector[T] {
  val appId: String
  val hostAddress: String
  val hostPort: Int
  def post(url: URL, body: JValue): T
  def put(url: URL, body: JValue): T
}
