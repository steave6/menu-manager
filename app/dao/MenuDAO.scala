package dao

import java.sql.Date
import javax.inject.Inject

import models.Menu
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future
//import play.api.db.slick.DatabaseConfigProvider
import slick.driver.PostgresDriver.api._

/**
  * Created by ujave on 17/03/17.
  */
class MenuDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                                  extends HasDatabaseConfigProvider[JdbcProfile] {

  private val menus = TableQuery[MenuTable]

  def all(): Future[Seq[Menu]] = db.run(menus.result)

  def getMenuByDate(d: java.time.LocalDate):Future[Seq[Menu]] = {
    val date: Date = Date.valueOf(d)
    val q = menus.filter(_.s_date === date).sortBy(_.order)

    db.run(q.result)
  }

  def insert(m: Menu): Future[Int] = db.run(menus.insertOrUpdate(m))

  def insertAll(ms: Seq[Menu]): Future[Option[Int]] = {
    val insertAction = menus.forceInsertAll(ms).transactionally
    db.run(insertAction)
  }

  private class MenuTable(tag: Tag) extends Table[Menu](tag, "menu") {

    def s_date = column[Date]("s_date", O.PrimaryKey)
    def three_meal = column[String]("three_meal", O.PrimaryKey)
    def order = column[String]("order", O.PrimaryKey)
    def recipe_code = column[String]("recipe_code")

    def * = (s_date, three_meal, order, recipe_code) <> ((Menu.apply _).tupled, Menu.unapply _)
  }
}
