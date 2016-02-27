/*

    Copyright (C) 2016 Mauricio Bustos (m@bustos.org)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package org.bustos.ebiexcelente

import javax.ws.rs.Path

import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout
import org.bustos.ebiexcelente.EbiExcelenteJsonProtocol._
import org.bustos.ebiexcelente.EbiExcelenteTables._
import org.slf4j.LoggerFactory
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.http.{DateTime, HttpCookie}
import spray.json._
import spray.routing._

class EbiExcelenteServiceActor extends HttpServiceActor with ActorLogging {

  override def actorRefFactory = context

  val ebiExcelenteRoutes = new EbiExcelenteRoutes {
    def actorRefFactory = context
  }

  def receive = runRoute(
    ebiExcelenteRoutes.routes ~
      get {getFromResourceDirectory("webapp")} ~
      get {getFromResource("webapp/index.html")})
}

trait EbiExcelenteRoutes extends HttpService {

  import java.net.InetAddress
  val logger = LoggerFactory.getLogger(getClass)
  val system = ActorSystem("ebiExcelenteSystem")
  implicit val defaultTimeout = Timeout(30 seconds)

  import system.dispatcher

  val ebiExcelenteData = system.actorOf(Props[EbiExcelenteData], "ebiExcelenteData")

  val routes = postEntry

  def redirectToHttps: Directive0 = {
    requestUri.flatMap { uri =>
      redirect(uri.copy(scheme = "https"), MovedPermanently)
    }
  }

  val secureCookies: Boolean = {
    // Don't require HTTPS if running in development
    val hostname = InetAddress.getLocalHost.getHostName
    hostname != "localhost" && !hostname.contains("pro")
  }

  val isHttpsRequest: RequestContext => Boolean = { ctx =>
    (ctx.request.uri.scheme == "https" || ctx.request.headers.exists(h => h.is("x-forwarded-proto") && h.value == "https")) && secureCookies
  }

  def enforceHttps: Directive0 = {
    extract(isHttpsRequest).flatMap(
      if (_) pass
      else redirectToHttps
    )
  }

  def postEntry = post {
    path("entry") {
      respondWithMediaType(`application/json`) { ctx =>
        val newEntry = ctx.request.entity.data.asString.parseJson.convertTo[Entry]
        val future = ebiExcelenteData ? newEntry
        future onSuccess {
          case EntrySuccessful => ctx.complete(200, "{\"Entry Successful\": \"true\"}")
          case EntryFailed => ctx.complete(500, "{\"Entry Failed\": \"true\"}")
        }
      }
    }
  }

}
