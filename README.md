# Simple structured loggin for zio logger

Thin helper to simplify structured logging, uses a LogAnnotation to create the structured context.
Can be used with any json library.

## example of using with circe json library

```Scala
package zio.logging.examples

import zio._

import zio.logging._
import zio.logging.structured._
import io.circe.syntax._
import io.circe.{ Decoder, Encoder }, io.circe.generic.auto._

object Examples extends zio.App {
  implicit val encodeSvalue: Encoder[SValue] = Encoder.instance {
    case StringSValue(s) => s.asJson
    case IntSValue(i)    => i.asJson
    case BoolSValue(b)   => b.asJson
    case NullSValue      => None.asJson
  }

  val env = structuredConsole(map => map.asJson.noSpaces)

  override def run(args: List[String]) =
    (for {
      _ <- logContext(("string", "value"), ("null", None), ("some", Some("thing"))) {
            log("info message") *>
              logContext(("int", 1), ("bool", false)) {
                log(LogLevel.Error)("error message")
              }

          }
    } yield 1).provideLayer(env)
}
```
