package edu.berkeley.cs.scadr
package comet

import scala.collection.mutable.HashMap

import scala.actors.Actor
import Actor._

import net.liftweb.http._
import net.liftweb.util._
import net.liftweb.http.js._
import net.liftweb.http.js.jquery.JqJsCmds._
import net.liftweb.common._
import JsCmds._
import JE._
import S._
import Helpers._
import scala.xml._

import edu.berkeley.cs.scadr.model._
import piql._

case class RegisterActor(a: ThoughtStreamActor)
case class UnregisterActor(a: ThoughtStreamActor)

object BroadcastActorRegistery extends Actor {
  private val actors = new HashMap[String, ThoughtStreamActor]

  def act = loop {
    react {
      case RegisterActor(a) =>
        println("registering actor for: " + a.user.name)
        actors += a.user.name -> a
      case UnregisterActor(a) =>
        println("unregistering actor for: " + a.user.name)
        actors -= a.user.name
      case (m: Thought , followers: List[User]) =>
        println("received thought " + m + ", followers: " + followers)
        followers.foreach(follower => {
            actors.get(follower.name) match {
              case Some(actor) =>
                println("calling send on thought " + m + " for follower " + follower.name)
                actor ! m
              case None =>
                println("Follower " + follower.name + " is not online")
            }
        })
      case e =>
        println("Unknown message: " + e)
    }
  }
}

class ThoughtStreamActor extends CometActor {
  override def defaultPrefix = Full("ts")

  def render = <span></span>

  override def localSetup {
    BroadcastActorRegistery ! RegisterActor(this)
    super.localSetup
  }

  override def localShutdown {
    BroadcastActorRegistery ! UnregisterActor(this)
    super.localShutdown
  }

  override def lowPriority : PartialFunction[Any, Unit] = {
    case t: Thought =>
      println("TSActor received thought " + t + ", for user " + user.name)
      partialUpdate(PrependHtml("appender", PiqlThought.thoughtToHTML(t)))
    case e =>
      println("Unknown message: " + e)
  }

  val user = PiqlUser.currentUser.open_!
}
