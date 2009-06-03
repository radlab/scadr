package edu.berkeley.cs.scadr.model

import edu.berkeley.cs.scads._
import java.security.MessageDigest
import net.liftweb.http.RequestVar  

object User {
	object currentUser extends RequestVar[User](null)
	val passHash = MessageDigest.getInstance("MD5")
 
	def create(username: String, password:String): User = {
	  val user = new User(username, hashPassword(password))
	  user.save
	  user
	}
 
	def find(username: String):User = {
	  val rec = SCADSCluster.useConnection((c) =>
	  	c.get("users", new StringKey(username).serialize)
	  )
   
	  deserialize(rec)
	}
 
	def listUsers(start: String, count: Int): Seq[User] = {
	  	val recSet = new SCADS.RecordSet()
		val rangeSet = new SCADS.RangeSet()
		recSet.setType(SCADS.RecordSetType.RST_RANGE)
		rangeSet.setStart_key((new StringKey(start)).serialize)
		rangeSet.setLimit(count)
		rangeSet.setOffset(0)
		recSet.setRange(rangeSet)
  
		val records = scala.collection.jcl.Conversions.convertList(SCADSCluster.useConnection(_.get_set("users", recSet)))

		records.map(deserialize(_))
	}
 
	def deserialize(rec: SCADS.Record): User = {
	  val username = StringKey.deserialize(rec.key, new java.text.ParsePosition(0)).stringVal
      val attrs = Json.parse(new String(rec.value)).asInstanceOf[Map[String, AnyRef]]
	  val nUser = new User(username, attrs("password").asInstanceOf[String])
	  val friends = attrs("friends").asInstanceOf[Seq[String]]
	  
	  friends.foreach((f) => nUser.addFriend(f))   
	  nUser
	}
 
	def hashPassword(password: String): String = {
	  passHash.digest(password.getBytes()).map((b:Byte) => Integer.toHexString(0xFF & b)).foldRight("")((x1:String, x2:String) => x1 + x2)
	}
}

case class User(username: String, password: String) {
	val friends = new scala.collection.mutable.ArrayBuffer[String]

	def save {
	  val data = Json.build(Map("password" -> password, "friends" -> friends))
	  val rec = new SCADS.Record(key.serialize, data.toString.getBytes())
	  
	  SCADSCluster.useConnection(_.put("users", rec))
	}

	def delete {
	  val rec = new SCADS.Record(key.serialize, null)
	  SCADSCluster.useConnection(_.put("users", rec))
	}

	def key: Key = {
	  new StringKey(username)
	}

	def addFriend(f: String) {
	  if(!friends.contains(f))
		  friends += f
	}

	def removeFriend(f: String) {
	  if(friends.contains(f))
		  friends -= f
	}

	def friendList: List[String] = {
      friends.toList
	}
 
	def myRecentThoughts(count: Int): Seq[Thought] = {
	  Thought.findRecentThoughts(username, count)
	}
 
	def friendsRecentThoughts(count: Int): Seq[Thought] = {
       (friends + username).flatMap((f) => Thought.findRecentThoughts(f, count)).toList.sort((x1, x2) => x2.timestamp < x1.timestamp)
	}
 
	def think(text: String): Thought = {
	  Thought.create(username, text)
	}
 
	def isFriend(f: User): Boolean = {
	  friends.contains(f.username)
	}

 	def ==(other: User): Boolean = (username == other.username)
}
