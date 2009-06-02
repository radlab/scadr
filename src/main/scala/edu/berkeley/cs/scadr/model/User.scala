package edu.berkeley.cs.scadr.model

import edu.berkeley.cs.scads._
import java.security.MessageDigest

object User {
	val passHash = MessageDigest.getInstance("MD5")
 
	def create(username: String, password:String): User = {
	  val user = new User(username, hashPassword(password))
	  user.save
	  user
	}
 
	def find(username: String):User = {
	  val value = new String(SCADSCluster.client.get("users", new StringKey(username).serialize).value)
	  val attrs = Json.parse(value).asInstanceOf[Map[String, AnyRef]]
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
	  
	  SCADSCluster.client.put("users", rec)
	}

	def delete {
	  val rec = new SCADS.Record(key.serialize, null)
	  SCADSCluster.client.put("users", rec)
	}

	def key: Key = {
	  new StringKey(username)
	}

	def addFriend(f: String) {
	  friends += f
	}

	def removeFriend(f: String) {
	  friends -= f
	}

	def friendList: List[String] = {
      friends.toList
	}
}
