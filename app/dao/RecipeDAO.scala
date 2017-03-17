package dao

/**
  * Created by ujave on 17/03/01.
  */
import javax.inject.Inject

import models.Recipe
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future
//import play.api.db.slick.DatabaseConfigProvider
//import slick.driver.H2Driver.api._
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

class RecipeDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                extends HasDatabaseConfigProvider[JdbcProfile] {

  private val Recipes = TableQuery[RecipeTable]

  def all(): Future[Seq[Recipe]] = db.run(Recipes.result)

  def insert(rec: Recipe): Future[Unit] = db.run(Recipes += rec).map { _ => () }

  private class RecipeTable(tag: Tag) extends Table[Recipe](tag, "recipes") {

    def code = column[String]("code", O.PrimaryKey)
    def name = column[String]("name")

    def * = (code, name) <> ((Recipe.apply _).tupled, Recipe.unapply _)
  }
}
