package com.mle.wicket.markup

import com.mle.util.{Regex, Log}
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.protocol.http.servlet.ServletWebRequest
import org.apache.wicket.markup.html.basic.Label
import java.security.cert.X509Certificate

/**
 * @author Mle
 */
class AccountPanel(id: String) extends Panel(id) with Log {
  val dn = clientDN getOrElse "No certificate"
  val cn = Regex.parse(dn, "CN=(.*),\\sO=.*") getOrElse "Unable to read CN"
  add(new Label("certChain", dn))
  add(new Label("cn", cn))

  /**
   *
   * @return the certificate chain, where the first element is the client certificate, or an empty sequence if there's no certificate
   */
  def certChain = {
    val servletRequest = getRequest.asInstanceOf[ServletWebRequest]
    val request = servletRequest.getContainerRequest
    Option(request.getAttribute("javax.servlet.request.X509Certificate")
      .asInstanceOf[Array[java.security.cert.X509Certificate]])
      .getOrElse(Array.empty).toSeq
  }

  def clientDN = certChain.headOption map extractDN

  def extractDN(cert: X509Certificate) = cert.getSubjectDN.getName
}