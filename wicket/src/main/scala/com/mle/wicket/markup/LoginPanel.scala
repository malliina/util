package com.mle.wicket.markup

import com.mle.util.Log
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.markup.html.form.{PasswordTextField, TextField, Form}
import com.mle.wicket.{BasicWebApplication, MySession}
import de.agilecoders.wicket.markup.html.bootstrap.common.NotificationPanel
import com.mle.web.wicket.model.RWModel

/**
 * @author Mle
 */
class LoginPanel(id: String) extends Panel(id) with Log {
  // try certificate authentication
  if (MySession.get().signIn(AccountPanel.certChain(getRequest))) {
    onAuthSuccess()
  }
  // cert auth failed, display form-based login
  var user: String = ""
  var pass: String = ""
  val alertPanel = new NotificationPanel("feedback")
  val loginForm = new Form("loginForm") {
    override def onSubmit() {
      if (MySession.get().signIn(user, pass)) {
        onAuthSuccess()
      } else {
        alertPanel error "Authentication failed"
      }
    }
  }

  def onAuthSuccess() {
    continueToOriginalDestination()
    setResponsePage(BasicWebApplication.get.getHomePage)
  }

  add(loginForm)
  loginForm.add(
    new TextField[String]("username", RWModel(user, user = _), classOf[String]),
    new PasswordTextField("password", RWModel(pass, pass = _)),
    alertPanel
  )
}