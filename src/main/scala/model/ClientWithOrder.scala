package model

case class ClientWithOrder(client: Client, order: Order) {
  def makeSell(stock: String, price: Int, amount: Int): ClientWithOrder =
    copy(client.sell(stock, price, amount), order.sell(amount))

  def makeBuy(stock: String, price: Int, amount: Int): ClientWithOrder =
    copy(client.buy(stock, price, amount), order.buy(amount))
}

