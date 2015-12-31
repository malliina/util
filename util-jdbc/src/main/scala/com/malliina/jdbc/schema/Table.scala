package com.malliina.jdbc.schema

import java.sql.ResultSet

import com.malliina.jdbc.Database

import scala.language.{implicitConversions, reflectiveCalls}

/**
 * TODO: Implement and use class Column instead of String to represent columns for obvious profit
 *
 * @author mle
 */
abstract class Table {
  implicit def int2question(count: Int) = new {
    def questionMarks = (1 to count) map (_ => "?")
  }

  def db: Database    // TODO: this should not be here?

  def schema: Schema

  def tableName: String

  def allSql = "select * from " + this

  def select[T](columns: String*)(where: (String, Any)*)(mapping: ResultSet => T) = {
    val selectCols = columns mkString ","
    val baseSql = "select " + selectCols + " from " + this
    val whereCond = if (where.isEmpty) "" else " where " + toWhereClause(where)
    db.query(baseSql + whereCond, toValues(where): _*)(mapping)
  }

  def insert(values: (String, Any)*) {
    if (values.size == 0)
      throw new IllegalArgumentException("No values to insert specified")
    val columns = values.map(_._1).mkString(",")
    val questionMarks = values.size.questionMarks.mkString(",")
    val insertSql = "insert into " + this + "(" + columns + ") values (" + questionMarks + ")"
    db.execute(insertSql, toValues(values): _*)
  }

  def delete(id: Int) {
    delete("id" -> id)
  }

  def delete(where: (String, Any)*) {
    if (where.size == 0)
      throw new IllegalArgumentException("No WHERE condition specified for DELETE operation")
    val target = where.map(_._1 + "=?").mkString(" and ")
    db execute("delete from " + this + " where " + target, toValues(where): _*)
  }

  def id(where: (String, Any)) = {
    val (column, value) = where
    db.head("select id from " + this + " where " + column + "=?", value)(_ getInt 1)
  }

  def update(values: (String, Any)*)(where: (String, Any)*) {
    if (where.size == 0)
      throw new IllegalArgumentException("No WHERE condition specified for UPDATE operation")
    val target = values.map(_._1 + "=?").mkString(",")
    val whereCond = where.map(_._1 + "=?").mkString(" and ")
    val allValues = toValues(values ++ where)
    db execute("update " + this + " set " + target + " where " + whereCond, allValues: _*)
  }

  def toWhereClause(where: Seq[(String, Any)]) = where.map(_._1 + "=?").mkString(" and ")

  def toValues(where: Seq[(String, Any)]) = where map (_._2)

  def qMarks(count: Int) = (1 to count).map("?")

  //  override def toString = schema + "." + name

}