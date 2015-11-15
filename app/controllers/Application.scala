package controllers

import javax.inject.Inject

import models._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._

case class GraphData(pair: String, brokers: List[String])

class Application @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action {
    Ok(views.html.index())
  }

  // return the form for graph page
  def getFormData() = {
    Form(
      mapping(
        "pair" -> default(text, "USDJPY"),
        "brokers" -> list(text)
      )(GraphData.apply)(GraphData.unapply)
    )
  }

  // simple graph
  def simple(pair: Option[String], brokers: List[String]) = Action { implicit request =>

    val formData = getFormData().bindFromRequest
    val graph = new Graph(formData)

    Ok(views.html.simple(graph, Swap.allPairs(), Swap.allBrokers()))
  }

  // diff graph
  def diff(pair: String, brokers: List[String]) = Action { implicit request =>

    val formData = getFormData().bindFromRequest
    val graph = new Diff(formData)

    Ok(views.html.diff(graph, Swap.allPairs(), Swap.allBrokers()))
  }


}