package edu.berkeley.cs.scadr.snippet

import scala.xml.NodeSeq
import net.liftweb.http._
import net.liftweb.util._
import S._
import Helpers._
import scala.xml._

import edu.berkeley.cs.scadr.model.User

class RegisterUser {
	def register(xhtml: NodeSeq): NodeSeq = {
	  var username = ""
	  var password = ""
	  def process() = {
		User.create(username, password)
		S.notice("User " + username + " created!")
	    redirectTo("index")
	  }
   
	  bind("e", xhtml,
	  		"username" -> SHtml.text(username, username = _),
	  		"password" -> SHtml.text(password, password = _),
	  		"submit" -> SHtml.submit("submit", process))
	}
}
