package com.mle.db

import com.mle.jdbc.schema.{UserGroupLinkTable, NameIdTable, UsersTable}

/**
 *
 * @author mle
 */
object Tables {
  type TestTable = DatabaseSettings.MySchema.DbTable

  object users extends TestTable with UsersTable {
    val id, name, password = Col()
  }

  object groups extends TestTable with NameIdTable {
    val id, name = Col()
  }

  object usergroup extends TestTable with UserGroupLinkTable {
    val user_id, group_id = Col()
  }

}
