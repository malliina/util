package com.malliina.azure

import com.microsoft.azure.storage.CloudStorageAccount

import scala.collection.JavaConversions._
import scala.util.Try

class StorageClient(accountName: String, accountKey: String) {
  private val connectionString =
    "DefaultEndpointsProtocol=https;" +
      "AccountName=" + accountName + ";" +
      "AccountKey=" + accountKey
  private val logContainerName = "$logs"
  val account = CloudStorageAccount parse connectionString
  val blobClient = account.createCloudBlobClient()
  val tableClient = account.createCloudTableClient()

  // Blob client operations
  def uris(containerName: String) =
    container(containerName).map(_.uris)

  def containers =
    blobClient.listContainers().map(new StorageContainer(_))

  def container(name: String) =
    cloudContainer(name).map(new StorageContainer(_))

  def logContainer =
    cloudContainer(logContainerName).map(new LogStorageContainer(_))

  private def cloudContainer(name: String) = Try {
    blobClient getContainerReference name
  }

  // Table client
  def tables = tableClient.listTables()

  //  blobClient.get
}
