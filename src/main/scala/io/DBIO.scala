package io

import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

case class DBIO[A](run: DBSession => A) {
  def map[B](f: A => B): DBIO[B] = DBIO(
    session =>
      f(run(session))
  )

  def flatMap[B](f: A => DBIO[B]): DBIO[B] = DBIO(
    session =>
      f(run(session)).run(session)
  )
}


class DBIORunner(ec: ExecutionContext) {
  def runLocalTx[A](dbio: DBIO[A]): Future[A] = Future {
    DB.localTx(dbio.run)
  }(ec)

  def runAutoCommit[A](dbio: DBIO[A]): Future[A] = Future {
    DB.autoCommit(dbio.run)
  }(ec)
}