package com.mle.jdbc.tests

import com.mle.jdbc.schema.{UserMgmtSchema, Schema}

/**
 *
 * @author mle
 */
object TestSchema extends Schema {

  val db = TestDb
  val name = "testdb"

  object Test extends DbTable {
    val myCol, hoihoi = Col()
  }

  object users extends DbTable {
    val id, name, password = Col()
  }

  object groups extends DbTable

  object usergroup extends DbTable

  object UserMgmtTables extends UserMgmtSchema(users, groups, usergroup)

}
