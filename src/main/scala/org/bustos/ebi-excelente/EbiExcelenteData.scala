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

import java.io.{FileOutputStream, File}

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory
import scala.collection.immutable.Iterable
import scala.util.Properties.envOrElse
import scala.slick.driver.MySQLDriver.simple._
import org.joda.time._
import scala.concurrent.duration._
import spray.json._

object EbiExcelenteData {

  val WestCoastId = "America/Los_Angeles"
  val hhmmssFormatter = DateTimeFormat.forPattern("hh:mm:ss a")
  val filedateFormatter = DateTimeFormat.forPattern("yyyymmddhhmmss")

  val db = {
    val mysqlURL = envOrElse("EBI_MYSQL_URL", "jdbc:mysql://mysql.bustos.org:3306/ebiexcelente")
    val mysqlUser = envOrElse("EBI_MYSQL_USER", "root")
    val mysqlPassword = envOrElse("EBI_MYSQL_PASSWORD", "")
    Database.forURL(mysqlURL, driver = "com.mysql.jdbc.Driver", user = mysqlUser, password = mysqlPassword)
  }
}

class EbiExcelenteData extends Actor with ActorLogging {

  import EbiExcelenteData._
  import EbiExcelenteTables._
  import EbiExcelenteJsonProtocol._

  val logger =  LoggerFactory.getLogger(getClass)

  implicit val defaultTimeout = Timeout(1 seconds)

  def receive = {
    case entry: Entry =>
      try {
        db.withSession { implicit session =>
          entryTable += entry
        }
      } catch {
        case _: Throwable => sender ! EntryFailed
      }
      sender ! EntrySuccessful
  }
}
