package com.mle.wicket.markup

import com.mle.util.Log
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.protocol.http.servlet.ServletWebRequest
import org.apache.wicket.markup.html.basic.Label
import com.mle.wicket.model.LDModel
import com.mle.auth.CertificateContainer

/**
 * @author Mle
 */
class AccountPanel(id: String) extends Panel(id) with Log {
  val certModel = LDModel(new CertificateContainer(certChain))
  val dn = LDModel(cert.dn getOrElse "No certificate")
  val cn = LDModel(cert.cn getOrElse "Unable to read CN")
  add(new Label("dn", dn))
  add(new Label("cn", cn))

  def cert = certModel.getObject

  /**
   *
   * @return the certificate chain, where the first element is the client certificate, or an empty sequence if there's no certificate
   */
  def certChain = {
    val servletRequest = getRequest.asInstanceOf[ServletWebRequest]
    val request = servletRequest.getContainerRequest
    Option(request.getAttribute("javax.servlet.request.X509Certificate")
      .asInstanceOf[Array[java.security.cert.X509Certificate]]
    ).getOrElse(Array.empty).toSeq
  }
}