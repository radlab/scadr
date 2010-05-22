package edu.berkeley.cs.scadr.snippet

import net.liftweb.http._
import net.liftweb.util._
import S._
import Helpers._
import scala.xml._

import edu.berkeley.cs.scadr.model._
import piql._

import java.util.Date

class ThoughtStream {
  private implicit val env = ScadsEnv.env

	def currentUser(xhtml: NodeSeq): NodeSeq = {
    val myThoughts = PiqlUser.currentUser.open_!.myThoughts(10)
    val otherThoughts = PiqlUser.currentUser.open_!.thoughtstream(10)

    val thoughts = (myThoughts ++ otherThoughts).sortWith(_.timestamp > _.timestamp).take(10)

	  thoughts.flatMap((t) =>
	    bind("t", xhtml,
	    	"username" -> t.owner.name,
	    	"timestamp" -> (new Date(t.timestamp * 1000L)).toString, // TODO: better formatting
	    	"text" -> t.text))
  }

	def think(xhtml: NodeSeq): NodeSeq = {
	  var thought = ""
	  def processThought() = {
      try {
        PiqlThought.create(PiqlUser.currentUser.open_!, thought)
        S.notice("Thought recorded!")
      } catch {
        case e => 
          S.error("Error when recording thought: " + e.getMessage)
      }
	  }

    def recordThought(t: String) = {
      thought = t
    }

	  bind("e", xhtml,
	  		"thought" -> SHtml.text(thought, recordThought),
	  		"submit" -> SHtml.submit("submit", processThought))
	}
}
