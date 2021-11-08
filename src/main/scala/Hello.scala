
import io.{DBIO, DBIORunner}
import repository.{Foo, FooRepository}
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings, DBSession}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

object Hello extends App {
  val dbContainer = new DBContainer {}

  val id = "5"
  val f = for {
    create <- FooRepository.create(Foo(id, "nyan"))

   // runLocalTxでは一連のIOの中で例外がある場合はcommitせずにrollbackする事を確認する
   // runAutoCommitでは一つ一つのsqlの実行のタイミングでcommitを行う（今回の例ではcreateは成功、で次の行で例外が投げられるのでfetchは実行されない）
   _ <- DBIO[Nothing]((_: DBSession) => throw new RuntimeException("にゃんこえくせぷしょん"))

    fetch <- FooRepository.fetch(id)
  } yield (fetch)

  val g = dbContainer.dbioRunner.runLocalTx(f)
//  val g  = dbContainer.dbioRunner.runAutoCommit(f)

  println(Await.result(g, Duration.Inf))

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

  private val ec = ExecutionContext.Implicits.global
  val dbioRunner = new DBIORunner(ec)
}

