package com.mle.web.wicket

import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.protocol.http.WebApplication

/**
 * @author Mle
 */

trait PageMounting extends WebApplication {
  /**
   * Mounts the pages to the path given by their class name
   */
  def mount[T <: WebPage](pageClazz: Class[T]) {
    mountPage("/" + pageClazz.getSimpleName, pageClazz)
  }
}