import model.{Client, ClientWithOrder, Order}
import org.scalatest.{FlatSpec, Matchers}
import service.{ExchangeService, ResourceManager}

class ExchangeServiceSpec extends FlatSpec with Matchers {


//  it should "" in {
//    val clients = ResourceManager.parseClients("clients.txt", ResourceManager.parseFromResource)
//    val orders = ResourceManager.parseOrders("orders.txt", ResourceManager.parseFromResource)
//    val exchangeService = new ExchangeService(clients.map(c => (c.name, c)).toMap)
//    exchangeService.matchOrders(orders)
//
//  }

  "processOrders" should "fully process orders that completely match each other" in {
    val exchangeService = new ExchangeService(Map.empty)
    val c1 = Client("C1", Map("$" -> 0, "A" -> 20, "B" -> 0, "C" -> 0, "D" -> 0))
    val c2 = Client("C2", Map("$" -> 20, "A" -> 0, "B" -> 0, "C" -> 0, "D" -> 0))
    val o1 = Order(c1.name, "s", "A", 1, 20)
    val o2 = Order(c2.name, "b", "A", 1, 20)
    val result = exchangeService.processOrders(ClientWithOrder(c1, o1), ClientWithOrder(c2, o2))

    result.isChanged shouldBe true
    result.sell.client.name shouldBe "C1"
    result.sell.client.balance("$") shouldBe 20
    result.sell.client.balance("A") shouldBe 0
    result.sell.order.clientName shouldBe "C1"
    result.sell.order.amount shouldBe 0

    result.buy.client.name shouldBe "C2"
    result.buy.client.balance("$") shouldBe 0
    result.buy.client.balance("A") shouldBe 20
    result.buy.order.clientName shouldBe "C2"
    result.buy.order.amount shouldBe 0
  }

  "processOrders" should "partially process orders that has different amounts" in {
    val exchangeService = new ExchangeService(Map.empty)
    val c1 = Client("C1", Map("$" -> 0, "A" -> 20, "B" -> 0, "C" -> 0, "D" -> 0))
    val c2 = Client("C2", Map("$" -> 20, "A" -> 0, "B" -> 0, "C" -> 0, "D" -> 0))
    val o1 = Order(c1.name, "s", "A", 1, 20)
    val o2 = Order(c2.name, "b", "A", 1, 10)
    val result = exchangeService.processOrders(ClientWithOrder(c1, o1), ClientWithOrder(c2, o2))

    result.isChanged shouldBe true
    result.sell.client.name shouldBe "C1"
    result.sell.client.balance("$") shouldBe 10
    result.sell.client.balance("A") shouldBe 10
    result.sell.order.clientName shouldBe "C1"
    result.sell.order.amount shouldBe 10

    result.buy.client.name shouldBe "C2"
    result.buy.client.balance("$") shouldBe 10
    result.buy.client.balance("A") shouldBe 10
    result.buy.order.clientName shouldBe "C2"
    result.buy.order.amount shouldBe 0
  }
}
