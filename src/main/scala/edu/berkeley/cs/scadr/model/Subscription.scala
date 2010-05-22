package edu.berkeley.cs.scadr.model

import net.liftweb.http._
import net.liftweb.common._

import piql._

object PiqlSubscription {
  private implicit val env = ScadsEnv.env

  def subscribe(owner: User, target: User): Subscription = {
    val s = new Subscription
    s.owner = owner
    s.target = target
    s.approved = true
    s.save
    s
  }
}
