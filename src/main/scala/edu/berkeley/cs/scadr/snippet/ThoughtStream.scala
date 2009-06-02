package edu.berkeley.cs.scadr.snippet

import edu.berkeley.cs.scadr.model.Thought
import edu.berkeley.cs.scadr.model.Json

class ThoughtStream {
	def currentUser = {
	  val thoughts = Thought.findRecentThoughts("marmbrus", 5)
   
	 /* <div> {	  
	    thoughts.map((t) => <p> {"thought: '" + t.text + "' - " + t.username} </p>)
	  } </div>*/
   
	  <div>{Json.build(List("test", "test2"))}</div>
	}
}
