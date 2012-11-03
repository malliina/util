package com.mle.wicket.markup

import org.apache.wicket.markup.html.WebPage

/**
 *
 * @author mle
 */
class Login extends WebPage {
  add(new LoginPanel("loginPanel"))
}
