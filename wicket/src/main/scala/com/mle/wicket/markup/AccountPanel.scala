package com.mle.wicket.markup

import com.mle.util.Log
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.protocol.http.servlet.ServletWebRequest
import com.mle.wicket.model.LDModel
import com.mle.auth.CertificateContainer
import com.mle.wicket.component.SLabel
import org.apache.wicket.request.Request

/**
 * @author Mle
 */
class AccountPanel(id: String) extends Panel(id) with Log {
  val certModel = LDModel(new CertificateContainer(AccountPanel.certChain(getRequest)))
  add(
    SLabel("dn", cert.dn),
    SLabel("cn", cert.cn)
  )

  def cert = certModel.getObject

  /**
   *
   * @return the certificate chain, where the first element is the client certificate, or an empty sequence if there's no certificate
   */

}

object AccountPanel {
  def certChain(req: Request) = {
    val servletRequest = req.asInstanceOf[ServletWebRequest]
    val request = servletRequest.getContainerRequest
    Option(request.getAttribute("javax.servlet.request.X509Certificate")
      .asInstanceOf[Array[java.security.cert.X509Certificate]]
    ).getOrElse(Array.empty).toSeq
  }
}