package edu.berkeley.cs.scadr.snippet

import net.liftweb.http._
import net.liftweb.util._
import S._
import Helpers._
import scala.xml._

import edu.berkeley.cs.scadr.model.User
import edu.berkeley.cs.scadr.model.Json

class ThoughtStream {
	def currentUser(xhtml: NodeSeq): NodeSeq = {
	  val thoughts = User.currentUser.friendsRecentThoughts(10)

	  thoughts.flatMap((t) =>
	    bind("t", xhtml,
	    	"username" -> t.username,
	    	"timestamp" -> t.timestamp,
	    	"text" -> t.text))
     }

	def think(xhtml: NodeSeq): NodeSeq = {
	  var thought = ""
	  def processThought() = {
	    User.currentUser.think(thought)
	    S.notice("Thought recorded!")
	  }

	  bind("e", xhtml,
	  		"thought" -> SHtml.text(thought, thought = _),
	  		"submit" -> SHtml.submit("submit", processThought))
	}
}
