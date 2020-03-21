package zio.logging

import zio._
import zio.console._
import zio.clock._

package object structured {

  sealed trait SValue
  case object NullSValue             extends SValue
  case class StringSValue(s: String) extends SValue
  case class IntSValue(i: Int)       extends SValue
  case class BoolSValue(b: Boolean)  extends SValue

  private val structure = LogAnnotation[Map[String, SValue]](
    name = "structure",
    initialValue = Map.empty,
    combine = (map, toMerge) => map ++ toMerge,
    render = _.toString()
  )

  def structuredConsole(writer: Map[String, SValue] => String): ZLayer[Console with Clock, Nothing, Logging] =
    zio.logging.Logging.make((context, line) =>
      for {
        date   <- currentDateTime.orDie
        level  = context.get(LogAnnotation.Level)
        values = context.get(structure)
        init   = Seq(("date", date.toString()), ("level", level.render), ("message", line))
        _      <- putStrLn(writer(toSValues(init) ++ values))
      } yield ()
    )

  def logContext[A, R <: Logging, E, A1](values: (String, Any)*)(zio: ZIO[R, E, A1]): ZIO[Logging with R, E, A1] =
    logLocally(structure(toSValues(values)))(zio)

  private def toSValues(values: Seq[(String, Any)]): Map[String, SValue] =
    values.map { case (s, v) => (s, toSValue(v)) }.toMap

  private def toSValue(value: Any): SValue = value match {
    case svalue: SValue => svalue
    case i: Int         => IntSValue(i)
    case b: Boolean     => BoolSValue(b)
    case Some(v)        => toSValue(v)
    case None           => NullSValue
    case other          => StringSValue(other.toString())
  }
}
