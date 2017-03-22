package controllers

import java.sql.Date
import java.time.LocalDate
import javax.inject.Inject

import dao.MenuDAO
import models.Menu
import net.liftweb.json.JsonAST.JValue
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

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
    def futureMap(ms: Seq[Menu]) = {
      val ml = for { m <- ms if(!ms.isEmpty) } yield MenuParse.serializeMenu(m)
      ml match {
        case Nil => Ok(MenuParse.serializeMenu(Menu(Date.valueOf("2000-1-1"), 1, 0, null)))
        case _:Vector[String] => Ok(MenuParse.mergeJsString(ml))
      }
    }
    val param = dstr.split("-").toList
    val d:String = param.take(3).mkString("-")
    val tm:Int = param.last.toInt
    val rjson: Future[Seq[Menu]] = menuDao.getMenuPerMeal(d, tm)
    rjson.map(futureMap)
  }

  def insertMenu() = Action(parse.tolerantJson) { request =>
    try {
      val rdata = request.body
      val ml = MenuParse.createMenu(rdata.toString())
      val dml = ml.filter(m => m.recipe_code == "")
      val iml = ml.filter(m => m.recipe_code != "")
      val iresult = menuDao.insertAll(iml)
      val dresult = menuDao.deleteRows(dml)

      Ok("update success")
    } catch {
      case e:Exception => Ok(e.toString)
    }
  }

  def menuList() = Action {
    val menu = new models.CookingMenu("test")

    val menulist = Json.toJson(menu.getMenuList())
    val json = Json.toJson(menulist)
    Ok(json)
  }
}

object MenuParse {
  import net.liftweb.json._
  import net.liftweb.json.Serialization.{read, write}
  import net.liftweb.json.JsonParser
  import models.Menu

  case class MenuTable(Date: String, Three_Meal: Int, Menu: List[MenuClass])
  case class MenuClass(order: Int, code: String)

  def createMenu(js: String): List[Menu] = {
    implicit val formats = DefaultFormats
    val pjs = parse(js)
    val mtex = pjs.extract[List[MenuTable]]

    val menul = for {
      mt <- mtex
      mc <- mt.Menu if mc.code != null
    } yield Menu(Date.valueOf(mt.Date), mt.Three_Meal, mc.order, mc.code)

    return menul
  }

  def serializeMenu(m: Menu): String = {
    val mjson = write(m)(Menu.formats)
    mjson
  }

  def mergeJsString(sl: Seq[String]): String = {
    val jmerged = sl.map(s => parse(s)).foldLeft(parse("")){(acc, x) => acc merge x}
    jmerged match {
      case JNothing => "{}"
      case _:JValue => compact(render(jmerged))
    }
  }
}