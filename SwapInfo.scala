package esplo.currency

import java.text.DateFormat
import java.time._
import java.util.Date


case class SwapInfo(
                     broker: String,
                     pair: CurrencyPair,
                     date: LocalDate,
                     num: Option[Int], // None means that there is no information about the number of days
                     buy: Price,
                     sell: Price
                     ) {

  // convert to DB class
  def serialize: SwapInfoForDB = {
    SwapInfoForDB(
      broker,
      pair.toString,
      getDateForMongo,
      num,
      buy.value,
      sell.value,
      buy.currency.name
    )
  }

  override def toString = {
    val format2 = DateFormat.getDateInstance
    s"broker: $broker $pair $date buy:$buy sell:$sell num:$num"
  }

  def getDateForMongo: Date = {
    Date.from(
      date.atStartOfDay(ZoneId.of("UTC")).toInstant
    )
  }
}


// swap-info class to write DB
case class SwapInfoForDB(
                          broker: String,
                          pair: String,
                          date: Date,
                          num: Option[Int],
                          buy: Long,
                          sell: Long,
                          currency: String
                          ) {

  // convert to normal class
  def deserialize: SwapInfo = {
    val swapCurrency = CurrencyFormatter.str2Currency(currency).get
    SwapInfo(
      broker,
      CurrencyFormatter.str2CurrencyPair(pair).get,
      LocalDate.ofEpochDay(date.getTime),
      num,
      new Price(swapCurrency, buy),
      new Price(swapCurrency, sell)
    )
  }
}
