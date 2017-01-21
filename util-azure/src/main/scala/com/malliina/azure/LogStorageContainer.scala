package com.malliina.azure

import java.util.EnumSet

import com.microsoft.azure.storage.OperationContext
import com.microsoft.azure.storage.blob.{BlobListingDetails, BlobRequestOptions, CloudBlobContainer}

import scala.collection.JavaConversions._

class LogStorageContainer(cont: CloudBlobContainer)
  extends StorageContainer(cont) {
  def logUris = cont.listBlobs("blob", true, EnumSet.noneOf(classOf[BlobListingDetails]), new BlobRequestOptions, new OperationContext).map(_.getUri)
}
