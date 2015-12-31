package com.malliina.auth

import java.security.cert.X509Certificate
import com.malliina.util.Regex
import com.malliina.exception.ParseException

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