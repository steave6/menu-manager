package controllers

import javax.inject.Inject

import dao.RecipeDAO
import models.Recipe
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

/**
  * Created by ujave on 17/03/01.
  */
class Recipe @Inject() (recDao: RecipeDAO) extends Controller {

  def index = Action.async {
    val item: Future[String] = Future(recDao.all().toString)
    println("test: " + item)
//    recDao.all().map { result => Ok(Json.toJson(result.toString()))}
    recDao.all().map { result => Ok(result.toString())}
//    Ok(Json.toJson(item))
  }

  val catForm = Form(
    mapping(
      "code" -> text(),
      "name" -> text()
    )(Recipe.apply)(Recipe.unapply)
  )
}