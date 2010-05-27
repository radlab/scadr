package edu.berkeley.cs.scadr.snippet

import scala.xml.NodeSeq
import net.liftweb.http._
import net.liftweb.util._
import S._
import Helpers._
import scala.xml._

import edu.berkeley.cs.scadr.model._

import piql._

class Subscriptions {

  private implicit val env = ScadsEnv.env

  def subscriptions(xhtml: NodeSeq): NodeSeq = {
    var following = List[User]()
    try {
      following = PiqlUser.currentUser.open_!.myFollowing(5000).toList
    } catch {
      case e =>
        S.error("general_error", Text("Could not find followers: " + e.getMessage))
    }

    def deleteSubscriptionTo(target: User) {
      try {
        val subscriptions = PiqlUser.currentUser.open_!.mySubscriptionTo(target.key)
        if (subscriptions.isEmpty)
          throw new IllegalStateException("subscription does not exist to target: " + target.name)
        val subscription = subscriptions.head
        subscription.delete
      } catch {
        case e =>
          S.error("general_error", Text("Could not delete subscription: " + e.getMessage))
      }
    }

    following.flatMap(s => bind("s", xhtml,
          "target" -> s.name,
          "delete" -> SHtml.link("subscription", () => deleteSubscriptionTo(s), Text("Unsubscribe"))))
  }

	def add(xhtml: NodeSeq): NodeSeq = {
    var username = ""
    def handle() = {
      case class SelfSubscribeException(val username: String) extends RuntimeException("Cannot subscribe to self")
      try {
        val curUser = PiqlUser.currentUser.open_!
        val targetUser = PiqlUser.find_!(username)
        if (curUser.name == targetUser.name)
          throw new SelfSubscribeException(username)
        PiqlSubscription.subscribe(curUser, targetUser)
      } catch {
        case NoSuchUsernameException(username) =>
          S.error("general_error", Text("No such user"))
        case SelfSubscribeException(username) =>
          S.error("general_error", Text("Cannot subscribe to self"))
        case e =>
          S.error("general_error", Text("Failed to subscribe: " + e.getMessage))
      }
    }

    bind("e", xhtml,
        "username" -> SHtml.text(username, (u: String) => username = u),
        "submit"   -> SHtml.submit("Subscribe", handle))
  }

}
