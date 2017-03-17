package controllers

import java.time.LocalDate
import javax.inject.Inject

import dao.MenuDAO
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class SenderData @Inject() (menuDao: MenuDAO) extends Controller {

  def targetMeal() = Action {
    val cooking = Map("code" -> Json.toJson(1))
    val cooking2 = Map("code" -> Json.toJson(2))
    val cooking3 = Map("code" -> Json.toJson(3))
    val days = Map(
      "one" -> Seq(cooking, cooking2, cooking3),
      "two" -> Seq(cooking2),
      "three" -> Seq(cooking, cooking3)
    )
    val json: JsValue = Json.toJson(days)

    Ok(json)
  }
  def targetMeal2(dstr: String) = Action.async {
    val t_date = LocalDate.parse(dstr)
    menuDao.getMenuByDate(t_date).map(res => Ok(Json.toJson(res)))
//    Ok(json)
  }

  def menuList() = Action {
    val menu = new models.CookingMenu("test")

    val menulist = Json.toJson(menu.getMenuList())
    val json = Json.toJson(menulist)
    Ok(json)
  }
}
