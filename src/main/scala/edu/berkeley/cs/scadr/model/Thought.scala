package edu.berkeley.cs.scadr.model

import java.util.Date
import scala.xml._

import piql._

object PiqlThought {
  private implicit val env = ScadsEnv.env
	def create(user: User, text: String): Thought = {
    val t = new Thought
    // timestamp is seconds past epoch
    t.timestamp = (System.currentTimeMillis / 1000L).toInt
    t.owner = user
    t.text = text
    t.save
    t
	}

  def formatThoughtTimestamp(thought: Thought) = (new Date(thought.timestamp * 1000L)).toString

  def thoughtToHTML(thought: Thought): NodeSeq = {
    <h3>{thought.text}</h3>
    <div class="summary">- thought by {thought.owner.name} at {formatThoughtTimestamp(thought)}</div>
  }
}
