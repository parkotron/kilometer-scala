package io.kilometer.client.response

import com.twitter.finagle.http.Response
import com.twitter.logging.Logger

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ResponseLogger {
  private val logger = Logger.get(getClass)

  def log(f:Future[Response]) = {
    f.map { r =>
      r.getStatusCode match {
        case 200 =>
        case x => s"kilometerRequest:failure:$x:${r.getContentString}"
      }
    }
  }
}
