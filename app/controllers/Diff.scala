package controllers

import esplo.currency.SwapInfoForDB
import play.api.data.Form

/**
 * graph for watching the differences among brokers
 */
class Diff(formData: Form[GraphData]) extends Graph(formData) {

  val brokerPairs = brokers.combinations(2).toList

  /**
   * create map (date -> (swap1 +- swap2))
   */
  override def dateSwaps = allDate.map(date => {
    brokerPairs.map(brokerPair => {
      val broker1 = brokerPair(0)
      val broker2 = brokerPair(1)

      val swap1 = swaps.filter(info =>
        info.broker == broker1 && info.date == date && info.pair == pair)
      val swap2 = swaps.filter(info =>
        info.broker == broker2 && info.date == date && info.pair == pair)

      if (swap1.isEmpty || swap2.isEmpty)
        None
      else {
        if (swap1.size != 1 || swap2.size != 1)
          System.err.println(s"duplicated swap info! ${swap1} ${swap2}")
        val b1 = swap1.head
        val b2 = swap2.head
        val label = s"${broker1}-${broker2}"

        Some(SwapInfoForDB(label, pair, date,
          None, b1.buy + b2.sell, b1.sell + b2.buy, pair))
      }
    })
  })

  // show two comparing brokers
  override val brokerCols = brokerPairs.map(pair => {
    val label1 = s"${pair(0)} buy & ${pair(1)} sell"
    val label2 = s"${pair(0)} sell & ${pair(1)} buy"
    s"{ label: '$label1 (JPY)', 'type':'number' }, { label: '$label2 (JPY)', 'type':'number' }"
  }).toList

}
