package com.malliina.jdbc.schema

import com.malliina.jdbc.Database
import com.malliina.util.Reflection

/**
 *
 * @author mle
 */
abstract class Schema {
  val db: Database
  val name: String

  override def toString = name

  abstract class DbTable extends JdbcTable {
    def db = Schema.this.db

    def schema = Schema.this

    override def toString = schema + "." + tableName
  }

}

abstract class JdbcTable extends Table {
  def tableName = Reflection.className(this)

  class Col extends Column {
    def schema = JdbcTable.this.schema

    def table = JdbcTable.this

    def name = Reflection.fieldName(JdbcTable.this, this)
  }

  object Col {
    def apply() = new Col
  }

}