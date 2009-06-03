package edu.berkeley.cs.scadr.snippet

import scala.xml.NodeSeq
import net.liftweb.http._
import net.liftweb.util._
import S._
import Helpers._
import scala.xml._

import edu.berkeley.cs.scadr.model.User

class Friends {
	def listUsers(xhtml: NodeSeq):NodeSeq = {
			def addFriend(f: User) = {
				User.currentUser.addFriend(f.username)
				S.notice("added " + f.username + " as a friend")
				User.currentUser.save
			}
   
			def removeFriend(f: User) = {
				User.currentUser.removeFriend(f.username)
				S.notice("removed " + f.username + " as a friend")
				User.currentUser.save
			}


			val users = User.listUsers("", 10)
			def pickAction(f: User) = 
				if(User.currentUser.isFriend(f))
					SHtml.submit("remove friend", () => removeFriend(f))
                else
                    SHtml.submit("add friend", () => addFriend(f))
   
			users.filter((u) => (u.username != User.currentUser.username)).flatMap((u) => 
				bind("u", xhtml,
					"username" -> u.username,
					"toggle" -> pickAction(u)
			))
	}

}
