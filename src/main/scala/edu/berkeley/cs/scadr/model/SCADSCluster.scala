package edu.berkeley.cs.scadr.model

import edu.berkeley.cs.scads.StorageNode

object SCADSCluster {
	val client: SCADS.Storage.Client = new StorageNode("127.0.0.1", 9000).getClient()
}
