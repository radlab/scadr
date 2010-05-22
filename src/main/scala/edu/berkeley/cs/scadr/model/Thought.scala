package edu.berkeley.cs.scadr.model

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
}
