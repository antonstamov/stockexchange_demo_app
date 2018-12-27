package service

import java.io.{File, FileInputStream, IOException, PrintWriter}

import model.{Client, Order}

import scala.io.Source

object ResourceManager {

  def parseFromFile(filePath: String): List[List[String]] = {
    Source.fromFile(filePath).getLines.map(_.split("\t").toList).toList
  }

  def parseFromResource(resourcePath: String): List[List[String]] = {
    Source.fromInputStream(getClass.getResourceAsStream(resourcePath)).getLines.map(_.split("\t").toList).toList
  }

  def parseClients(filePath: String, resourceReadFunc: String => List[List[String]] = parseFromFile): List[Client] = {
    resourceReadFunc(filePath).map { row =>
      Client(
        name = row(0),
        balance = Map(
          "$" -> row(1).toInt,
          "A" -> row(2).toInt,
          "B" -> row(3).toInt,
          "C" -> row(4).toInt,
          "D" -> row(5).toInt)
      )
    }
  }

  def parseOrders(filePath: String, resourceReadFunc: String => List[List[String]] = parseFromFile): List[Order] = {
    resourceReadFunc(filePath).map { row =>
      Order(
        clientName = row(0),
        operation = row(1),
        stock = row(2),
        price = row(3).toInt,
        amount = row(4).toInt
      )
    }
  }

  def exportResult(filepath: String, clients: List[Client]): Unit = {
    val pw = new PrintWriter(new File(filepath))
      clients.sortBy(_.name).foreach(c => {
        pw.write(c.name + "\t")
        List("$", "A","B","C","D").foreach(s => pw.write(c.balance(s) + "\t"))
        pw.write("\n")
      })
      pw.close()
  }
}
