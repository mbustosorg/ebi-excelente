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

import akka.util.ByteString
import org.joda.time._
import java.sql.Timestamp
import org.joda.time.format.{DateTimeFormat, ISODateTimeFormat, DateTimeFormatter}

import scala.slick.driver.MySQLDriver.simple._
import spray.json._

object EbiExcelenteTables {

  // Base case classes
  case class Entry(subject: String, adjective: String, timestamp: DateTime, language: String, donation: Float)
  case class EntriesRequest()
  // Utility classes
  case class Entries(entries: List[Entry])
  case class EntrySuccessful()
  case class EntryFailed()

  val entryTable = TableQuery[EntryTable]

  val formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss")

  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)
  implicit def dateTime =
    MappedColumnType.base[DateTime, Timestamp](
       dt => new Timestamp(dt.getMillis),
       ts => new DateTime(ts.getTime)
    )
}

object EbiExcelenteJsonProtocol extends DefaultJsonProtocol {

  import EbiExcelenteTables._

  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {
    private val parserISO: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()
    private val parserMillisISO: DateTimeFormatter = ISODateTimeFormat.dateTime()
    override def write(obj: DateTime) = JsString(parserISO.print(obj))
    override def read(json: JsValue) : DateTime = json match {
      case JsString(s) =>
        try {
          parserISO.parseDateTime(s)
        } catch {
          case _: Throwable => parserMillisISO.parseDateTime(s)
        }
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }

  // Base case classes
  implicit val entry = jsonFormat5(Entry)
}

class EntryTable(tag: Tag) extends Table[EbiExcelenteTables.Entry](tag, "Entry") {

  import EbiExcelenteTables.dateTime

  def subject = column[String]("subject")
  def adjective = column[String]("adjective")
  def timestamp = column[DateTime]("timestamp")
  def language = column[String]("language")
  def donation = column[Float]("donation")

  def * = (subject, adjective, timestamp, language, donation) <> (EbiExcelenteTables.Entry.tupled, EbiExcelenteTables.Entry.unapply)

}

