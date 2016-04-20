package io.kilometer.collector

import org.joda.time.DateTime
import org.json4s._
import org.json4s.native._

trait FinagleBackedCollector {
  import com.twitter.finagle.Service
  import com.twitter.finagle.http
  import com.twitter.finagle.http._

  import com.twitter.finagle.builder._
  import java.net.InetSocketAddress
  import java.net.URL
  import com.twitter.conversions.time._

  import scala.concurrent.{Future, Promise}
  import com.twitter.util.{Future => TwitterFuture, Throw, Return}

  def fromTwitter[A](twitterFuture: TwitterFuture[A]): Future[A] = {
    val promise = Promise[A]()
    twitterFuture respond {
      case Return(a) => promise success a
      case Throw(e)  => promise failure e
    }
    promise.future
  }

  object underlying {
    def client(hostAddress: String, hostPort: Int) = {
      val clientBuilder = ClientBuilder()
        .tls(hostAddress)
        .hosts(new InetSocketAddress(hostAddress, hostPort))
        .codec(http.Http())
        .hostConnectionLimit(20)
        .tcpConnectTimeout(4 seconds)
        .timeout(4 seconds)
        .retries(2)
        .keepAlive(false)
        .failFast(true)

      clientBuilder.build
    }

    def request(url: URL, authHeader: String) = {
      http.RequestBuilder()
        .setHeader("Customer-App-Id", authHeader)
        .setHeader("Timestamp", DateTime.now.getMillis.toString)
        .setHeader("Content-Type", "application/json")
        .url(url)
    }

    def build(client: Service[Request, Response], url: URL, authHeader: String, body: JValue) = {
      (request(url, authHeader), com.twitter.io.Buf.ByteArray.Shared(compactJson(renderJValue(body)).getBytes))
    }

    def post(client: Service[Request, Response], url: URL, authHeader: String, body: JValue) = {
      val (req, postBody) = build(client, url, authHeader, body)
      val response: TwitterFuture[Response] = client(req.buildPost(postBody))
      fromTwitter[Response](response)
    }

    def put(client: Service[Request, Response], url: URL, authHeader: String, body: JValue) = {
      val (req, putBody) = build(client, url, authHeader, body)
      val response: TwitterFuture[Response] = client(req.buildPut(putBody))
      fromTwitter[Response](response)
    }
  }
}

trait KilometerEvent {
  val event_name: String
  val event_properties: Map[String, String]
}

trait IdentifiedKilometerEvent extends KilometerEvent {
  val user_id: String
  val event_type: String = "identified"
}