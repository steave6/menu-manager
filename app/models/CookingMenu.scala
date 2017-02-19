package models

import play.api.libs.json._

/**
  * Created by ujave on 17/02/19.
  */
class CookingMenu(mealType: String) {
  def mealMaps = Seq(
    Map("code" -> Json.toJson(1), "name" -> Json.toJson("cake")),
    Map("code" -> Json.toJson(2), "name" -> Json.toJson("milk")),
    Map("code" -> Json.toJson(3), "name" -> Json.toJson("rice")),
    Map("code" -> Json.toJson(4), "name" -> Json.toJson("hamburger")),
    Map("code" -> Json.toJson(5), "name" -> Json.toJson("candy")),
    Map("code" -> Json.toJson(6), "name" -> Json.toJson("pudding")),
    Map("code" -> Json.toJson(7), "name" -> Json.toJson("pie")),
    Map("code" -> Json.toJson(8), "name" -> Json.toJson("roast beef")),
    Map("code" -> Json.toJson(9), "name" -> Json.toJson("salad")),
    Map("code" -> Json.toJson(10), "name" -> Json.toJson("fish")),
    Map("code" -> Json.toJson(11), "name" -> Json.toJson("meat ball"))
  )

  def getMealByType(meal: String): Map[String, String] = {
    Map("test" -> "test")
  }

  def getMenuList(): JsValue = {
    Json.toJson(mealMaps)
  }

}
