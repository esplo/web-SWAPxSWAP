package esplo.currency


object CurrencyFormatter {

  // 通貨ペアを表す文字列を通貨ペアクラスに変換
  def str2CurrencyPair(str: String, delim: String = "/"): Option[CurrencyPair] = {

    def convert(name1: String, name2: String): Option[CurrencyPair] = {
      val base = str2Currency(name1)
      val quote = str2Currency(name2)

      if (base.isEmpty || quote.isEmpty)
        None
      else
        Some(CurrencyPair(base.get, quote.get))
    }

    str.split(delim).toList match {
      // delimiterが無く、6文字の場合は3文字ずつで分ける
      case List(s: String) if s.length == 6 =>
        convert(str.substring(0, 3), str.substring(3, 6))
      case List(s1: String, s2: String) =>
        convert(s1, s2)
      case _ =>
        None
    }
  }

  // 通貨名から通貨クラスに変換
  def str2Currency(str: String): Option[Currency] = {
    // 名前を全通貨から検索
    val hitNames = Currency.allCurrencies().filter(_.names.exists(_.toLowerCase == str.toLowerCase))

    // 複数見つかった場合、登録ミスが疑われるのでassert
    assert(hitNames.size <= 1, s"there are currencies that have the same name $str!")

    if (hitNames.size == 1)
      Some(hitNames.head)
    else {
      // 見つからなかった場合、登録されていない通貨なので、エラー出力のみでNoneを返す
      System.err.println(s"there is no currency named $str!")
      None
    }
  }

}
