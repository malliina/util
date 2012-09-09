package com.mle.wicket.markup

import com.mle.util.Log
import com.mle.wicket.component.SAjaxSubmitLink
import java.util.Date
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.atmosphere.{Subscribe, EventBus}
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.{TextField, Form}
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.Model

/**
 * @author Mle
 */

class Atmosphere(id: String) extends Panel(id) with Log {
  val timeLabel = new Label("time", Model.of("start")).setOutputMarkupId(true)
  val messageLabel = new Label("message", Model.of("-")).setOutputMarkupId(true)
  add(timeLabel, messageLabel)
  val form = new Form("form")
  add(form)
  val input = new TextField("input", Model.of(""))
  val submitLink = new SAjaxSubmitLink("send")(target => {
    log info "Sending"
    EventBus.get.post("Message: " + input.getModelObject)
    //    AtmosphereApplication.get.eventBus.post(input.getModelObject)
  })
  form add(input, submitLink)
  setVersioned(false)

  @Subscribe
  def updateTime(target: AjaxRequestTarget, event: Date) {
    timeLabel setDefaultModelObject event.toString
    target add timeLabel
  }

  @Subscribe
  def receiveMessage(target: AjaxRequestTarget, message: String) {
    messageLabel setDefaultModelObject message
    target add messageLabel
  }
}