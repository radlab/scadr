package edu.berkeley.cs.scadr

import _root_.java.io.File
import _root_.junit.framework._
import Assert._
import _root_.scala.xml.XML
import _root_.net.liftweb.util._
import edu.berkeley.cs.scadr.model._

object AppTest {
	def suite: Test = {
		val suite = new TestSuite(classOf[AppTest])
		suite
}

def main(args : Array[String]) {
	_root_.junit.textui.TestRunner.run(suite)
}
}

/**
* Unit test for simple App.
*/
class AppTest extends TestCase("app") {

	def assert_same(x: Seq[Thought], y: Seq[Thought]) = assert(x.equalsWith(y)((x: Thought, y: Thought) => x == y), "not equal: " + x + ", " + y)

	def testThoughts(){
		val thoughts = (1 to 10).map((i) => {
			Thread.sleep(10)
			Thought.create("simpleUser", "I'm thinking " + i)
		}).toList

		assert_same(thoughts.reverse.take(5), Thought.findRecentThoughts("simpleUser", 5))
	}

	def testUsers() {
		var retUser: User = null;
		val u1 = User.create("user1", "mypass")
		u1.addFriend("user2")
		u1.addFriend("user3")
		u1.save

		retUser = User.find("user1")
		assert(u1.username == retUser.username, "Usernames differ:" + u1.username + " " + retUser.username)
		assert(u1.password == retUser.password, "Passwords differ:" + u1.password + " " + retUser.password)
		assert(retUser.friendList.contains("user2"), "Missing user2")
		assert(retUser.friendList.contains("user3"), "Missing user3")

		val users = User.listUsers("", 100)
		assert(users.contains(u1), "User1 is missing from list " + users.toList)
	}

	/**
	* Tests to make sure the project's XML files are well-formed.
	*
	* Finds every *.html and *.xml file in src/main/webapp  and tests to make sure they are well-formed.
	*/
	def testXml() = {
		var failed: List[File] = Nil

		def handledXml(file: String) =
			file.endsWith(".xml")

			def handledXHtml(file: String) =
				file.endsWith(".html") || file.endsWith(".htm") || file.endsWith(".xhtml")

				def wellFormed(file: File) {
		if (file.isDirectory)
			for (f <- file.listFiles) wellFormed(f)

			if (file.isFile && handledXml(file.getName)) {
				try {
					XML.loadFile(file)
				} catch {
				case e: _root_.org.xml.sax.SAXParseException => failed = file :: failed
				}
			}
		if (file.isFile && handledXHtml(file.getName)) {
			PCDataXmlParser(new java.io.FileInputStream(file.getAbsolutePath)) match {
			case Full(_) => // file is ok
			case _ => failed = file :: failed
			}
		}
	}

	wellFormed(new File("src/main/webapp"))

	val numFails = failed.size
	if (numFails > 0) {
		val fileStr = if (numFails == 1) "file" else "files"
		val msg = "Malformed XML in " + numFails + " " + fileStr + ": " + failed.mkString(", ")
		println(msg)
		fail(msg)
	}
	}
}
