package models

import play.api.libs.json._
import java.sql.Date

case class Recipe(code: String, name: String)
object Recipe {
  implicit val format: Format[Recipe] = Json.format[Recipe]
}

case class Menu(s_date: Date, three_meal: String, order: String, recipe_code: String)
object Menu {
  implicit val format: Format[Menu] = Json.format[Menu]
}