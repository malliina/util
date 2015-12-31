package com.malliina.azure

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
  private val logContainerName = "$logs"
  val account = CloudStorageAccount parse connectionString
  val blobClient = account.createCloudBlobClient()
  val tableClient = account.createCloudTableClient()

  // Blob client operations
  def uris(containerName: String) =
    container(containerName).uris

  def containers =
    blobClient.listContainers().map(new StorageContainer(_))

  def container(name: String) =
    new StorageContainer(cloudContainer(name))

  def logContainer =
    new LogStorageContainer(cloudContainer(logContainerName))

  private def cloudContainer(name: String) = {
    blobClient getContainerReference name
  }

  // Table client
  def tables = tableClient.listTables()
//  blobClient.get
}