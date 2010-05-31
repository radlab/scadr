package edu.berkeley.cs.scadr.snippet

import net.liftweb.http._
import net.liftweb.util._
import net.liftweb.http.js._
import net.liftweb.http.js.jquery.JqJsCmds._
import JsCmds._
import JE._
import S._
import Helpers._
import scala.xml._

import edu.berkeley.cs.scadr.comet._
import edu.berkeley.cs.scadr.model._

import piql._

class ThoughtStream {
  private implicit val env = ScadsEnv.env

	def currentUser(xhtml: NodeSeq): NodeSeq = {
    val myThoughts = PiqlUser.currentUser.open_!.myThoughts(10)
    val otherThoughts = PiqlUser.currentUser.open_!.thoughtstream(10)

    val thoughts = (myThoughts ++ otherThoughts).sortWith(_.timestamp > _.timestamp).take(10)

	  thoughts.flatMap((t) =>
	    bind("t", xhtml,
	    	"username" -> t.owner.name,
	    	"timestamp" -> PiqlThought.formatThoughtTimestamp(t),
	    	"text" -> t.text))
  }

  private val THOUGHT_MAX_LEN = 500

  private def processThought(thought: String): Option[Thought] = {
    try {
      val truncated =
        if (thought.length > THOUGHT_MAX_LEN)
          thought.substring(0, THOUGHT_MAX_LEN)
        else thought
      val t = PiqlThought.create(PiqlUser.currentUser.open_!, truncated)
      val msg = (t, PiqlUser.currentUser.open_!.myFollowers(100).toList)
      //println("sending msg to BActor: " + msg)
      BroadcastActorRegistery ! msg
      Some(t)
    } catch {
      case e =>
        S.error("Error when recording thought: " + e.getMessage)
        None
    }
  }

  def ajaxThink(xhtml: Group): NodeSeq = {
    var thought = ""
    def process(s: String): JsCmd = {
      processThought(s) match {
        case Some(t) =>
          Seq(SetValById("thought", Str("")), PrependHtml("appender", PiqlThought.thoughtToHTML(t)))
        case None =>
          Alert("Unable to save message - server error")
      }
    }
    SHtml.ajaxForm(
      bind("e", xhtml,
          "thought" -> SHtml.text("", (t: String) => thought = t, "id" -> "thought"),
          "submit" -> SHtml.submit("submit", () => { })) ++
      SHtml.hidden(() => { process(thought) }))
  }

	def think(xhtml: NodeSeq): NodeSeq = {
	  var thought = ""


    def recordThought(t: String) = {
      thought = t
    }

	  bind("e", xhtml,
	  		"thought" -> SHtml.text(thought, recordThought),
	  		"submit" -> SHtml.submit("submit", () => { processThought(thought) }))
	}
}
