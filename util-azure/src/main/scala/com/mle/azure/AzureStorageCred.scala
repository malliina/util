package com.mle.azure

import com.mle.util.Implicits._
import com.mle.util.security.BaseCredentialReader

/**
 *
 * @author mle
 */
case class AzureStorageCredential(accountName: String, accountKey: String)

object AzureStorageCredentialReader extends AzureStorageCredentialReader

trait AzureStorageCredentialReader extends BaseCredentialReader[AzureStorageCredential] {
  val userHomeCredential = userHome / "keys" / "azure-storage.sec"

  def resourceCredential = "security/azure-storage.sec"

  override def loadOpt = fromEnvOpt orElse fromUserHomeOpt

  def fromMapOpt(map: Map[String, String]): Option[AzureStorageCredential] =
    for (a <- map get "account_name";
         key <- map get "account_key")
    yield AzureStorageCredential(a, key)
}

