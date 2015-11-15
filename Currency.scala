package esplo.currency


object Currency {

  case object USD extends Currency(List("米ドル", "ドル"))

  case object EUR extends Currency(List("ユーロ"))

  case object JPY extends Currency(List("円"))

  case object GBP extends Currency(List("ポンド"))

  case object AUD extends Currency(List("豪ドル"))

  case object CHF extends Currency(List("スイスフラン"))

  case object CAD extends Currency(List("カナダドル"))

  case object MXN extends Currency(List("ペソ"))

  case object CNY extends Currency(List("元", "人民元"))

  case object NZD extends Currency(List("NZドル", "ＮＺドル"))

  case object RUB extends Currency(List("ルーブル"))

  case object ZAR extends Currency(List("南アランド", "ランド"))

  case object HKD extends Currency(List("香港ドル"))

  // 検索のため、全列挙したリストを返す
  def allCurrencies(): List[Currency] = {
    List(
      USD,
      EUR,
      JPY,
      GBP,
      AUD,
      CHF,
      CAD,
      MXN,
      CNY,
      NZD,
      RUB,
      ZAR,
      HKD
    )
  }

}


sealed abstract class Currency(subName: List[String]) {
  val name = toString
  val names = name :: subName
}
