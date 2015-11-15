package esplo.currency


case class Price(currency: Currency, value: Long) {
  override def toString = s"$value$currency"
}
