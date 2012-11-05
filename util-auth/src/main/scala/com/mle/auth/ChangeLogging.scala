package com.mle.auth

import com.mle.util.Log

/**
 * @author Mle
 */
trait ChangeLogging[T] extends UserManager[T] with Log {
  abstract override def addUser(user: T, password: String) {
    super.addUser(user, password)
    log info "Added user: " + user
  }

  abstract override def removeUser(user: T) {
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

  abstract override def assign(user: T, group: String) {
    super.assign(user, group)
    log info "Added user: " + user + " to group: " + group
  }

  abstract override def revoke(user: T, group: String) {
    super.revoke(user, group)
    log info "Removed user: " + user + " from group: " + group
  }

  abstract override def setPassword(user: T, newPassword: String) {
    super.setPassword(user, newPassword)
    log info "Password changed for user: " + user
  }
}
