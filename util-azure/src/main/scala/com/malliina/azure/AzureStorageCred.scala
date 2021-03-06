package com.malliina.azure

import com.malliina.file.StorageFile
import com.malliina.util.BaseConfigReader

case class AzureStorageCredential(accountName: String, accountKey: String)

object AzureStorageCredentialReader extends AzureStorageCredentialReader

trait AzureStorageCredentialReader extends BaseConfigReader[AzureStorageCredential] {
  override def filePath = Option(userHome / "keys" / "azure-storage.sec")

  override def loadOpt = fromEnvOpt orElse fromUserHomeOpt

  def fromMapOpt(map: Map[String, String]): Option[AzureStorageCredential] =
    for (a <- map get "account_name";
         key <- map get "account_key")
      yield AzureStorageCredential(a, key)
}
