package model

case class Order(clientName: String, operation: String, stock: String, price: Int, amount: Int) {

  def sell(amount: Int): Order = copy(amount = this.amount - amount)
  def buy(amount: Int): Order = copy(amount = this.amount - amount)
}
