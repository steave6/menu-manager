package dao

import java.sql.Date
import javax.inject.Inject

import models.Menu
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.dbio.DBIOAction
import slick.driver.JdbcProfile

import scala.concurrent.Future
//import play.api.db.slick.DatabaseConfigProvider
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by ujave on 17/03/17.
  */
class MenuDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                                  extends HasDatabaseConfigProvider[JdbcProfile] {

  private val menus = TableQuery[MenuTable]

  def all(): Future[Seq[Menu]] = db.run(menus.result)

  def getMenuByDate(d: String):Future[Seq[Menu]] = {
    val date: Date = Date.valueOf(d)
    val q = menus.filter(_.s_date === date).sortBy(_.order)

    db.run(q.result)
  }

  def getMenuPerMeal(d: String, tm: Int): Future[Seq[Menu]] = {
    val date: Date = Date.valueOf(d)
    val q = menus.filter(x => x.s_date === date && x.three_meal === tm).sortBy(_.order)

    db.run(q.result)
  }

  def insert(m: Menu): Future[Int] = db.run(menus.insertOrUpdate(m))

  def insertAll(ms: Seq[Menu]) = {
    val iAction = for {
      m <- ms
    } yield menus.insertOrUpdate(m)

    val iresult = db.run(DBIOAction.sequence(iAction).transactionally)
    iresult
  }

  def deleteAll(): Future[Int] = {
    val d_all = menus.delete
    db.run(d_all)
  }
  def deletePerRow(m: Menu): Future[Int] = {
    val dpr = menus.filter(x => x.s_date === m.s_date && x.three_meal === m.three_meal && x.order === m.order).delete
    db.run(dpr)
  }
  def deleteRows(lm: Seq[Menu]) = {
    val dresult = for {m <- lm} yield deletePerRow(m)
    dresult
  }

  private class MenuTable(tag: Tag) extends Table[Menu](tag, "menu") {

    def s_date = column[Date]("s_date", O.PrimaryKey)
    def three_meal = column[Int]("three_meal", O.PrimaryKey)
    def order = column[Int]("order", O.PrimaryKey)
    def recipe_code = column[String]("recipe_code")

    def * = (s_date, three_meal, order, recipe_code) <> ((Menu.apply _).tupled, Menu.unapply _)
  }
}
