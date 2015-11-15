package controllers

import java.text.SimpleDateFormat
import java.util.TimeZone

import models.Swap
import play.api.data._

/**
 * this class is a simple Graph drawer and parent for other graphs
 */
class Graph(val formData: Form[GraphData]) {

  // for convenient access
  val pair = formData.get.pair
  val brokers: List[String] = formData.get.brokers

  protected val swaps = Swap.get(Some(pair), brokers)
  protected val allDate = swaps.map(_.date).distinct


  /**
   * create map (date -> swaps)
   */
  protected def dateSwaps = allDate.map(date => {
    brokers.map(broker => {
      val swap = swaps.filter(info =>
        info.broker == broker && info.date == date && info.pair == pair)
      if (swap.isEmpty)
        None
      else {
        if (swap.size != 1) {
          System.err.println("duplicated swap info!")
        }
        Some(swap.head)
      }
    })
  })

  protected val brokerCols = brokers.flatMap(broker => {
    List(
      s"{ label: '$broker buy  (JPY)', 'type':'number' }",
      s"{ label: '$broker sell  (JPY)', 'type':'number' }"
    )
  })

  /**
   * legend of columns
   */
  def cols =
    ("{ label: 'date', 'type':'string' }" :: brokerCols).mkString(",")

  /**
   * return json data of a graph
   */
  def jsondata = {
    // check if dateSwaps has no data
    if (dateSwaps.flatMap(t => t).isEmpty) ""
    else getJSONData
  }

  /**
   * child classes should override this method
   */
  protected def getJSONData = {
    val data = dateSwaps.zip(allDate).map(swapDateList => {

      val fullFormat = new SimpleDateFormat("yyyy/MM/dd")
      fullFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
      val shortFormat = new SimpleDateFormat("MMdd")
      fullFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

      val swaps = swapDateList._1
      val date = swapDateList._2

      val innerData = {
        swaps.flatMap {
          case None =>
            List("{}", "{}")
          case Some(info) =>
            List(s"{ v: ${info.buy} }", s"{ v: ${info.sell} }")
        }
      }

      // if all elements in a innerData are "{}", filter it
      if( ! innerData.forall(_ == "{}"))
         Some( "{ c: [ " +
          s"  { v: '${fullFormat.format(date)}', f: '${shortFormat.format(date)}' }, " +
          innerData.mkString(",") +
          "] }" )
      else
        None
    })

    data.flatMap(d=>d).mkString(",")
  }

}
