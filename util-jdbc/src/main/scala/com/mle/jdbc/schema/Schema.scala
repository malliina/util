package com.mle.jdbc.schema

import com.mle.jdbc.Database
import com.mle.util.Reflection

/**
 *
 * @author mle
 */
abstract class Schema {
  val db: Database
  val name: String

  override def toString = name

  abstract class DbTable extends Table {
    def db = Schema.this.db

    def schema = Schema.this

    def tableName = Reflection.className(this)

    override def toString = schema + "." + tableName

    class Col extends Column {
      def schema = DbTable.this.schema

      def table = DbTable.this

      def name = Reflection.fieldName(DbTable.this, this)
    }

    object Col {
      def apply() = new Col
    }

  }

}
