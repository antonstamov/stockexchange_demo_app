package model

case class Client(name: String, balance: Map[String, Int]) {
  def sell(stock: String, price: Int, amount: Int): Client = {
    copy(balance = balance + ("$" -> (balance("$") + price * amount)) + (stock -> (balance(stock) - amount)))
  }
  def buy(stock: String, price: Int, amount: Int): Client = {
    copy(balance = balance + ("$" -> (balance("$") - price * amount)) + (stock -> (balance(stock) + amount)))
  }
}

