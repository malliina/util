package com.mle.auth

import java.security.cert.X509Certificate
import com.mle.util.Regex
import com.mle.exception.ParseException

/**
 *
 * @author mle
 */
class CertificateContainer(certChain: Seq[X509Certificate]) {
  val dn = certChain.headOption.map(extractDN)
    .getOrElse(throw new SecurityException("Empty certificate chain"))

  val cn = Regex.parse(dn, "CN=([^,]*),\\sO=.*")
    .getOrElse(throw new ParseException("Unable to extract CN from DN: " + dn))

  def extractDN(cert: X509Certificate) = cert.getSubjectDN.getName
}