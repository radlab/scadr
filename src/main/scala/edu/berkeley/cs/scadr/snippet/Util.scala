package edu.berkeley.cs.scadr.snippet

import scala.xml.NodeSeq
import net.liftweb.http._
import net.liftweb.util._
import net.liftweb.common._
import S._
import Helpers._
import scala.xml._

import edu.berkeley.cs.scadr.model._

class ScadrUtil {
  def registerLink(xhtml: NodeSeq): NodeSeq = {
	  bind("e", xhtml,
        "registerLink" -> SHtml.link("register", () => redirectTo("register"), Text("register")))
  }

  def logoutLink(xhtml: NodeSeq): NodeSeq = {
	  bind("e", xhtml,
        "logoutLink" -> SHtml.link("logout", () => { PiqlUser.currentUser.set(Empty); redirectTo("index") }, Text("logout")))
  }
}
