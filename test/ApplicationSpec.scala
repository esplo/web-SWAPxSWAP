import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("SWAPxSWAP")
    }

    "render the simple graph page" in new WithApplication{
      val simpleGraph = route(FakeRequest(GET, "/simple")).get

      status(simpleGraph) must equalTo(OK)
      contentType(simpleGraph) must beSome.which(_ == "text/html")
      contentAsString(simpleGraph) must contain ("swap graph - simple")
    }

    "render the simple graph page for DMM USD/JPY" in new WithApplication{
      val simpleGraph = route(FakeRequest(GET, "/simple?pair=USDJPY&brokers%5B%5D=DMM")).get

      status(simpleGraph) must equalTo(OK)
      contentType(simpleGraph) must beSome.which(_ == "text/html")
      contentAsString(simpleGraph) must contain ("swap graph - simple")
      contentAsString(simpleGraph) must contain ("USDJPY DMM")
    }

    "render the simple graph page for none broker" in new WithApplication{
      val simpleGraph = route(FakeRequest(GET, "/simple?pair=USDJPY&brokers=")).get

      status(simpleGraph) must equalTo(OK)
      contentType(simpleGraph) must beSome.which(_ == "text/html")
      contentAsString(simpleGraph) must contain ("swap graph - simple")
      contentAsString(simpleGraph) must contain ("brokerを1つ以上選んで、drawボタンを押してください。")
    }

    "render the diff graph page" in new WithApplication{
      val simpleGraph = route(FakeRequest(GET, "/diff?pair=USD%2FJPY")).get

      status(simpleGraph) must equalTo(OK)
      contentType(simpleGraph) must beSome.which(_ == "text/html")
      contentAsString(simpleGraph) must contain ("swap graph - diff")
    }
  }
}
