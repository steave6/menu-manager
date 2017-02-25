package controllers

import javax.inject.Inject

import play.api.Play.current
import play.api.mvc._
import play.api.db._

/**
  * Created by ujave on 17/02/26.
  */
class ScalaControllerInject @Inject()(db: Database) extends Controller {
  def index = Action {
    var outString = "Number is "
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT name as testkey FROM recipes")

      while (rs.next()) {
        outString += rs.getString("testkey")
      }
    } finally {
      conn.close()
    }
    Ok(outString)
  }

}
