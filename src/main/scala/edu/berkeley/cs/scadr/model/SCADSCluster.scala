package edu.berkeley.cs.scadr.model

import edu.berkeley.cs.scads.StorageNode

abstract class ObjectPool[PoolType] {
	def borrowObject(): PoolType
	def returnObject(o: PoolType)
}

class SimpleObjectPool[PoolType](c: () => PoolType) extends ObjectPool[PoolType] {
	val pool = new scala.collection.mutable.ArrayStack[PoolType]
	val creator = c

  	def borrowObject(): PoolType = {
  	  if(pool.isEmpty)
  	     c()
      else
         pool.pop
  	}

	def returnObject(o: PoolType) {
	  pool.push(o)
	}
}

object SCADSCluster {
	val connectionPool = new SimpleObjectPool[SCADS.Storage.Client](() => (new StorageNode("localhost", 9000)).getClient())

	def useConnection[ReturnType](f: SCADS.Storage.Client => ReturnType): ReturnType = {
			val client = connectionPool.borrowObject()
			val ret = f(client)
			connectionPool.returnObject(client)
			return ret
	}
}
