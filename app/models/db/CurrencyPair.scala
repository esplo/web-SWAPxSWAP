package esplo.currency


case class CurrencyPair(base: Currency, quote: Currency) {
  override def toString = base.toString + quote.toString
}
