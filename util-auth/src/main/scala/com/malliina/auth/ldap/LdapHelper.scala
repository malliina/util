package com.malliina.auth.ldap

import javax.naming.directory.{DirContext, ModificationItem, BasicAttribute, BasicAttributes}

/**
 *
 * @author mle
 */
object LdapHelper {
  def attributes(keyValues: (LdapAttributes.LdapAttribute, String)*): BasicAttributes = {
    val stringified = keyValues.map(kv => kv._1.toString -> kv._2)
    attributesStr(stringified: _*)
  }

  def attributeStr(attributeName: String, values: String*) = {
    val attr = new BasicAttribute(attributeName)
    values foreach attr.add
    attr
  }

  def attribute2(attributeName: LdapAttributes.LdapAttribute,
                 value: String*): BasicAttribute = {
    attributeStr(attributeName.toString, value: _*)
  }

  def attribute(attributeName: LdapAttributes.LdapAttribute,
                value: LdapAttributes.LdapAttribute*): BasicAttribute = {
    attribute2(attributeName, value.map(_.toString): _*)
  }

  def attributesStr(keyValues: (String, String)*): BasicAttributes = {
    val attrs = new BasicAttributes()
    keyValues foreach (kv => {
      val (key, value) = kv
      attrs put new BasicAttribute(key, value)
    })
    attrs
  }

  /**
   * @see DirContext.REMOVE_ATTRIBUTE etc.
   */
  def arrayModification(modAttribute: Int, kvs: (LdapAttributes.LdapAttribute, String)*): Array[ModificationItem] = {
    kvs.map(kv => {
      val (key, value) = kv
      new ModificationItem(modAttribute, attribute2(key, value))
    }).toArray
  }

  def updateModification(kv: (LdapAttributes.LdapAttribute, String)*): Array[ModificationItem] =
    arrayModification(DirContext.REPLACE_ATTRIBUTE, kv: _*)
}
