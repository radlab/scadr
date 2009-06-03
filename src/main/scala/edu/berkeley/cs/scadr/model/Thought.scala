package edu.berkeley.cs.scadr.model

import edu.berkeley.cs.scads._

object Thought {
	def create(user: String, text: String): Thought = {
		val t = new Thought(user, System.currentTimeMillis(), text)
		t.save
		return t
	}

	def findRecentThoughts(username: String, limit: Int): Seq[Thought] = {
		val recSet = new SCADS.RecordSet()
		val rangeSet = new SCADS.RangeSet()
		recSet.setType(SCADS.RecordSetType.RST_RANGE)
		rangeSet.setStart_key((new StringKey(username)).serialize)
		rangeSet.setEnd_key((new StringKey(username)).serialize + "0")
		rangeSet.setLimit(limit)
		rangeSet.setOffset(0)
		recSet.setRange(rangeSet)

		println(recSet)
		val recs = SCADSCluster.useConnection((c) =>
			c.get_set("thoughts", recSet)
		)

		val records = scala.collection.jcl.Conversions.convertList(recs)

		records.map(deserialize(_))
	}

	def deserialize(rec: SCADS.Record) = {
		val pos = new java.text.ParsePosition(0)
		val u = StringKey.deserialize(rec.key, pos)
		val t = NumericKey.deserialize(rec.key, pos)
		new Thought(u.stringVal, t.numericVal * -1, new String(rec.value))
	}
}

case class Thought(username: String, timestamp: Long, text: String) {
	def save {
		val rec =  new SCADS.Record(key, text.getBytes())

		SCADSCluster.useConnection(_.put("thoughts", rec))
	}

	def delete {
		val rec =  new SCADS.Record(key, null)
		SCADSCluster.useConnection(_.put("thoughts", rec))
	}

	def key: String = {
		(new StringKey(username)).serialize + (new NumericKey[Long](timestamp * -1)).serialize
	}
}
