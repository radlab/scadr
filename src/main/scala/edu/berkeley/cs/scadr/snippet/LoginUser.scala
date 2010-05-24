package edu.berkeley.cs.scadr.snippet

import scala.xml.NodeSeq
import net.liftweb.http._
import net.liftweb.util._
import net.liftweb.common._
import S._
import Helpers._
import scala.xml._

import edu.berkeley.cs.scadr.model._

class LoginUser {
  def login(xhtml: NodeSeq): NodeSeq = {
	  var username = ""
	  var password = ""
    def handle() = {
      PiqlUser.userByCredentials(username, password) match {
        case None => 
          S.error("general_error", Text("Invalid username and/or password"))
        case Some(user) =>
          PiqlUser.currentUser.set(Full(user))
          redirectTo("index")
      }
    }
	  bind("e", xhtml,
	  		"username" -> SHtml.text(username, (u: String) => username = u),
	  		"password" -> SHtml.password(password, (p: String) => password = p),
	  		"submit" -> SHtml.submit("Let me in!", handle))
  }
}
