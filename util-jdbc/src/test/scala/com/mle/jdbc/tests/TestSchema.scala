package com.mle.jdbc.tests

import com.mle.jdbc.schema.{UserMgmtSchema, Schema}
import com.mle.jdbc.DB

/**
 *
 * @author mle
 */
object TestSchema extends Schema {

  val db = DB
  val name = DB.schema

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
