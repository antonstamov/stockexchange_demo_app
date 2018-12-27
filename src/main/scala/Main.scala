import service.{ExchangeService, ResourceManager}

object Main extends App {

  val clientsPath = "clients.txt"
  val ordersPath = "orders.txt"

  var clients = ResourceManager.parseClients(clientsPath).map(c => (c.name, c)).toMap
  clients.foreach(println)
  val orders = ResourceManager.parseOrders(ordersPath)
  orders.foreach(println)

  val service = new ExchangeService(clients = clients)
  println("PROCESSED ORDERS")
  val start = System.currentTimeMillis()
  val po = service.matchOrders(orders)
  po.foreach(println)
  println(System.currentTimeMillis() - start)
  println(orders.size)
  println(po.size)
  println("CLIENTS")
  val pc = service.clients.values.toList
  pc.foreach(println)
  ResourceManager.exportResult("results.txt", pc)
}





