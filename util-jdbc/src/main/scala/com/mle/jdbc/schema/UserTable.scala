package com.mle.jdbc.schema

/**
 *
 * @author mle
 */
trait UsersTable extends NameIdTable {
  def password: Column
}

trait NameIdTable extends NameTable with IdTable


trait NameTable extends Table {
  def name: Column
}

trait IdTable extends Table {
  def id: Column
}

trait UserGroupLinkTable extends Table {
  def user_id: Column

  def group_id: Column
}