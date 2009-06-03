package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import auth._
import edu.berkeley.cs.scadr.model.User

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("edu.berkeley.cs.scadr")

    //Set up http authentication
    val roles = AuthRole("User")

    LiftRules.httpAuthProtectedResource.append{
      case(ParsePath("stream" :: _, _, _, _)) =>
        roles.getRoleByName("User")
    }

    LiftRules.httpAuthProtectedResource.append{
      case(ParsePath("users" :: _, _, _, _)) =>
        roles.getRoleByName("User")
    }

    LiftRules.authentication = HttpBasicAuthentication("scadr") {
      case(username: String, password: String, req) => {
        try {
        	val user = User.find(username)
        	assert(User.hashPassword(password) == user.password)
            userRoles(AuthRole("User"))
            User.currentUser(user)
            true
        }
        catch {
          case e: AssertionError => false
        }
      }
    }

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) ::
    			  Menu(Loc("Register", List("register"), "Register")) ::
    			  Menu(Loc("Thought Stream", List("stream"), "Thought Stream")) ::
    			  Menu(Loc("Find Friends", List("users"), "Find Friends")) ::
    		      Nil
    LiftRules.setSiteMap(SiteMap(entries:_*))
  }
}
