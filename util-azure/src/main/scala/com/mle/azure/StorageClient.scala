package com.mle.azure

import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount
import scala.collection.JavaConversions._

/**
 *
 * @author mle
 */
class StorageClient(accountName: String, accountKey: String) {
  private val connectionString =
    "DefaultEndpointsProtocol=http;" +
      "AccountName=" + accountName + ";" +
      "AccountKey=" + accountKey
  val account = CloudStorageAccount parse connectionString
  val blobClient = account.createCloudBlobClient()

  def uris(containerName: String) =
    container(containerName).uris

  def containers =
    blobClient.listContainers().map(new StorageContainer(_))

  def container(name: String) =
    new StorageContainer(cloudContainer(name))

  private def cloudContainer(name: String) = {
    blobClient getContainerReference name
  }
}