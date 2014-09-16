package com.mle.azure

import com.mle.file.StorageFile
import com.mle.util.BaseConfigReader

/**
 *
 * @author mle
 */
case class AzureStorageCredential(accountName: String, accountKey: String)

object AzureStorageCredentialReader extends AzureStorageCredentialReader

trait AzureStorageCredentialReader extends BaseConfigReader[AzureStorageCredential] {
  override def userHomeConfPath = userHome / "keys" / "azure-storage.sec"

  override def resourceCredential = "security/azure-storage.sec"

  override def loadOpt = fromEnvOpt orElse fromUserHomeOpt

  def fromMapOpt(map: Map[String, String]): Option[AzureStorageCredential] =
    for (a <- map get "account_name";
         key <- map get "account_key")
    yield AzureStorageCredential(a, key)
}

