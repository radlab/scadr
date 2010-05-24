package edu.berkeley.cs.scadr.model

import java.security.MessageDigest
import net.liftweb.http._
import net.liftweb.common._

import piql._

case class ExistingUsernameException(val username: String) extends RuntimeException(username)
case class NoSuchUsernameException(val username: String) extends RuntimeException(username)

case class UnimplementedException(val feature: String) extends RuntimeException("feature not implemented: " + feature)

object PiqlUser {
	object currentUser extends SessionVar[Box[User]](Empty)

  def loggedIn = currentUser.isDefined
  def loggedIn_? = loggedIn _

  def loggedOut = currentUser.isEmpty
  def loggedOut_? = loggedOut _

	private val passHash = MessageDigest.getInstance("SHA1")

	def hashPassword(password: String): String = {
	  passHash.digest(password.getBytes()).map((b:Byte) => Integer.toHexString(0xFF & b)).foldRight("")((x1:String, x2:String) => x1 + x2)
	}

  private implicit val env = ScadsEnv.env

	def create(username: String, password: String): User = userByName(username) match {
    case Some(_) => 
      throw new ExistingUsernameException(username)
    case None => 
      val u = new User
      u.name = username
      u.password = hashPassword(password)
      u.save
      u
	}

  def userByCredentials(username: String, password: String): Option[User] = userByName(username) match {
    case None => None
    case Some(user) => 
      if (user.password == hashPassword(password))
        Some(user)
      else 
        None
  }

  def userByName(username: String): Option[User] = {
    val qL = Queries.userByName(username)
    if (qL isEmpty)
      None
    else
      Some(qL.head)
  }


	def find(username: String): User = userByName(username).getOrElse(null)
  def find_!(username: String): User = userByName(username) match {
    case None => throw new NoSuchUsernameException(username)
    case Some(user) => user
  }


	def listUsers(start: String, count: Int): Seq[User] = {
    throw new UnimplementedException("listUsers")
	}


}
