package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import net.liftweb.json._
import java.sql.Date

case class Recipe(code: String, name: String)
object Recipe {
  implicit val format: Format[Recipe] = Json.format[Recipe]
}

case class Menu(s_date: Date, three_meal: Int, order: Int, recipe_code: String)
object Menu {
//  implicit val format: Format[Menu] = Json.format[Menu]
//  implicit val menuWrites: Writes[Menu] = (
//    (__ \ "Date").write[Date] and
//    (__ \ "Three_meal").write[Int] and
//    (__ \ "Menu").write(
//      (__ \ "order").write[Int] and
//        (__ \ "code").write[String]
//    )
//  )(unlift(Menu.unapply()))

  implicit val formats = Serialization.formats(NoTypeHints) + new MenuSerializer
  class MenuSerializer extends Serializer[Menu] {
    private val MenuClass = classOf[Menu]

    override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Menu] = {
      case (TypeInfo(MenuClass, _), json) => json match {
        case x => throw new MappingException("Cant convert")
      }
    }

    override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: Menu =>
        JObject(JField("date", JString(x.s_date.toString)) ::
                JField("three_meal", JInt(x.three_meal)) ::
                JField("menu", JArray(List(JObject(
                  JField("order", JInt(x.order)) ::
                  JField("code", JString(x.recipe_code)) :: Nil)))) ::
          Nil)
    }
  }
}