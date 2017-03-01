package models

import play.api.libs.json._

case class Recipe(code: String, name: String)

object Recipe {
  implicit val format: Format[Recipe] = Json.format[Recipe]
}