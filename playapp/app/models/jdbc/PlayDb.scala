package models.jdbc

import com.mle.jdbc.auth.MySQLConnectionProvider
import com.mle.util.Util
import com.mle.jdbc.{StatementLogging, Database}
import com.mle.jdbc.schema.Schema


/**
 *
 * @author mle
 */
object PlayDb {
  val dbInfo = Util.props("resources/playauth.test")
  val connInfo = Connectivity(dbInfo("db.uri"), dbInfo("db.user"), dbInfo("db.pass"))

  class ConnProvider extends MySQLConnectionProvider(connInfo.uri, connInfo.user, Some(connInfo.pass))

  object PlaySchema extends Schema {
    val db = new Database(new ConnProvider) with StatementLogging
    val name = "testdb"

    object testtable extends DbTable {
      val a = Col()
    }

  }

  case class Connectivity(uri: String, user: String, pass: String)

}

