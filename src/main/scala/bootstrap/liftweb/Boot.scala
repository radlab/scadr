package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.common._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import auth._
import piql._

import edu.berkeley.cs.scadr.model._
import edu.berkeley.cs.scadr.comet._

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("edu.berkeley.cs.scadr")

    LiftRules.loggedInTest = Full(() => { PiqlUser.loggedIn })

    LiftRules.dispatch.append {
      case Req("logout" :: Nil, _, GetRequest) => {
        PiqlUser.currentUser.set(Empty)
        S.redirectTo("index")
      }
    }

    BroadcastActorRegistery.start

    // Build SiteMap
    val entries =
            Menu(Loc("Home", List("index"), "Home")) ::
    			  Menu(Loc("Login", List("login"), "Login", If(PiqlUser.loggedOut_?, "Already logged in"))) ::
    			  Menu(Loc("Logout", List("logout"), "Logout", If(PiqlUser.loggedIn_?, "Not logged in"))) ::
    			  Menu(Loc("Register", List("register"), "Register", If(PiqlUser.loggedOut_?, "Already logged in"))) ::
    			  Menu(Loc("Thought Stream", List("stream"), "Thought Stream", If(PiqlUser.loggedIn_?, "Not logged in"))) ::
    			  Menu(Loc("Subscription", List("subscription"), "Subscriptions", If(PiqlUser.loggedIn_?, "Not logged in"))) ::
            Nil
    LiftRules.setSiteMap(SiteMap(entries:_*))
  }
}
