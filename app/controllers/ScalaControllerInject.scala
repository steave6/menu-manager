package controllers

/**
  * Created by ujave on 17/02/26.
  */
//class ScalaControllerInject @Inject()(db: Database) extends Controller {
//  def index = Action {
//    var outString = "Number is "
//    val conn = db.getConnection()
//
//    try {
//      val stmt = conn.createStatement
//      val rs = stmt.executeQuery("SELECT name as testkey FROM recipes")
//
//      while (rs.next()) {
//        outString += rs.getString("testkey")
//      }
//    } finally {
//      conn.close()
//    }
//    Ok(outString)
//  }
//
//}
