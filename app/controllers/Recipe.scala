package controllers

import javax.inject.Inject

import dao.RecipeDAO
import models.Recipe
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

/**
  * Created by ujave on 17/03/01.
  */
class Recipe @Inject() (recDao: RecipeDAO) extends Controller {

  def index = Action.async {
    recDao.all().map { result => Ok(Json.toJson(result)) }
  }

  val catForm = Form(
    mapping(
      "code" -> text(),
      "name" -> text()
    )(Recipe.apply)(Recipe.unapply)
  )
}