
import io.DBIORunner
import repository.{Foo, FooRepository}
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

object Hello extends App {
  val dbContainer = new DBContainer {}
  
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  val f = for {
    fetch1 <- dbContainer.dbioRunner.runLocalTx(
      for {
        _ <- FooRepository.create(Foo("6", "nyan"))
        fetch <- FooRepository.fetch("6")
      } yield fetch)
    fetch2 <- dbContainer.dbioRunner.runLocalTx(
      for {
        _ <- FooRepository.create(Foo("7", "nyan"))
        fetch <- FooRepository.fetch("7")
      } yield fetch)
  } yield (fetch1, fetch2)

  Await.result(f, Duration.Inf)
}

trait DBContainer {
  //todo: configから読む
  val url = "jdbc:postgresql://localhost:5432/postgres"
  val user = "root"
  val password = "root"


  //卍古卍
  Class.forName("org.postgresql.Driver")

  private val settings = ConnectionPoolSettings(
    initialSize = 5,
    maxSize = 20,
    connectionTimeoutMillis = 3000L,
    validationQuery = "select 1 from dual")

  //  ConnectionPool.add("default", url, user, password, settings)
  ConnectionPool.add("default", url, user, password)

  val ec = ExecutionContext.Implicits.global
  val dbioRunner = new DBIORunner(ec)
}

