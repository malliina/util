package com.mle.jdbc.tests

import com.mle.jdbc.tests.Tables.{users, groups, usergroup}
import com.mle.jdbc.schema.{Schema, UserMgmtSchema}

/**
 *
 * @author mle
 */
object TestSchema extends Schema {
  val db = TestDb
  val name = "testdb"
}

object Test extends TestSchema.DbTable {
  val myCol, hoihoi = Col()
}

object UserMgmtTables extends UserMgmtSchema(users, groups, usergroup)



