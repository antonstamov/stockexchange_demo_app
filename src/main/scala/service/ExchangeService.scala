package service

import model.{Client, ClientWithOrder, Order}

class ExchangeService(var clients: Map[String, Client]) {

  case class SFoldAcc(sells: List[Order], buys: List[Order], isChanged: Boolean)

  case class BFoldAcc(sellOrder: Order, buyOrders: List[Order], isChanged: Boolean)
  def matchOrders(orders: List[Order]): List[Order] = {
    // create List of sell and buy orders grouped by stock
    var sellsBuys = orders.groupBy(_.stock).values.map { orders =>
      val sells = orders.filter(_.operation == "s")
      val buys = orders.filter(_.operation == "b")
      (sells, buys)
    }.toList
    var isChanged = false
    do {
      val sellsBuysWithChange = sellsBuys.map { case (sells, buys) =>
        // iterate over sells list and collect results to SFoldAcc
        val sFoldResult = sells.foldRight(SFoldAcc(List.empty[Order], buys, isChanged = false)) { (s, acc) =>
          // iterate over buys list and collect results to BFoldAcc
          val bFoldResult = acc.buys
            .foldRight(BFoldAcc(s, List.empty, isChanged = false)) { (b, acc) =>

              val result = processOrders(ClientWithOrder(clients(acc.sellOrder.clientName), acc.sellOrder), ClientWithOrder(clients(b.clientName), b))
              // update clients map if orders were processed
              if (result.isChanged)
                clients = clients + (result.sell.client.name -> result.sell.client) + (result.buy.client.name -> result.buy.client)
              BFoldAcc(result.sell.order, acc.buyOrders :+ result.buy.order, acc.isChanged || result.isChanged)

            }
          SFoldAcc(acc.sells :+ bFoldResult.sellOrder, bFoldResult.buyOrders.filter(_.amount != 0), acc.isChanged || bFoldResult.isChanged)
        }
        ((sFoldResult.sells.filter(_.amount != 0), sFoldResult.buys), sFoldResult.isChanged)
      }.unzip
      isChanged = sellsBuysWithChange._2.contains(true)
      sellsBuys = sellsBuysWithChange._1

    } while (isChanged)
    sellsBuys.flatMap { case (s, b) => s ++ b }
  }

  case class ProcessOrdersResult(sell: ClientWithOrder, buy: ClientWithOrder, isChanged: Boolean)
  def processOrders(sell: ClientWithOrder, buy: ClientWithOrder): ProcessOrdersResult = {
    val price = sell.order.price
    val amount = List(sell.order.amount, buy.order.amount, buy.client.balance("$") / price, sell.client.balance(sell.order.stock)).min
    if (buy.order.price >= sell.order.price && amount > 0) {
      ProcessOrdersResult(
        sell.makeSell(sell.order.stock, price, amount),
        buy.makeBuy(sell.order.stock, price, amount),
        isChanged = true
      )
    } else ProcessOrdersResult(sell, buy, isChanged = false)
  }
}

