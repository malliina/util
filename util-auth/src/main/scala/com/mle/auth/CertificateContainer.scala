package com.mle.auth

import java.security.cert.X509Certificate
import com.mle.util.Regex
import com.mle.exception.ParseException

/**
 *
 * @author mle
 */
class CertificateContainer(certChain: Seq[X509Certificate]) {
  if(certChain.isEmpty)throw new
  val dn = certChain.headOption map extractDN

  val cn = dn.map(dName => Regex.parse(dName, "CN=([^,]*),\\sO=.*")
    .getOrElse(throw new ParseException("Unable to extract CN from DN: " + dName)))

  def extractDN(cert: X509Certificate) = cert.getSubjectDN.getName
}
