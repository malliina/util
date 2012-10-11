package com.mle.auth

import com.mle.util.Log

/**
 * @author Mle
 */
trait ChangeLogging extends UserManager with Log {
  abstract override def addUser(user: String, password: String) {
    super.addUser(user, password)
    log info "Added user: " + user
  }

  abstract override def removeUser(user: String) {
    super.removeUser(user)
    log info "Removed user: " + user
  }

  abstract override def addGroup(group: String) {
    super.addGroup(group)
    log info "Added group: " + group
  }

  abstract override def removeGroup(group: String) {
    super.removeGroup(group)
    log info "Removed group: " + group
  }

  abstract override def assign(user: String, group: String) {
    super.assign(user, group)
    log info "Added user: " + user + " to group: " + group
  }

  abstract override def revoke(user: String, group: String) {
    super.revoke(user, group)
    log info "Removed user: " + user + " from group: " + group
  }

  abstract override def setPassword(user: String, newPassword: String) {
    super.setPassword(user, newPassword)
    log info "Password changed for user: " + user
  }
}
