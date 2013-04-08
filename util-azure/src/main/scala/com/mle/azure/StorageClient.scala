package com.mle.azure

import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount

/**
 *
 * @author mle
 */
class StorageClient(accountName: String, accountKey: String) {
  private val connectionString =
    "DefaultEndpointsProtocol=http;" +
      "AccountName=" + accountName + ";" +
      "AccountKey=" + accountKey
  private val account = CloudStorageAccount parse connectionString

  def uris(containerName: String) =
    container(containerName).uris

  def container(name: String) =
    new StorageContainer(cloudContainer(name))

  private def cloudContainer(name: String) = {
    val client = account.createCloudBlobClient()
    client getContainerReference name
  }
}