package io

import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

case class DBIOContext(
                        dbSession: DBSession,
                        ec: ExecutionContext
                      )

case class DBIO[A](run: DBIOContext => Future[A]) {
  def map[B](f: A => B): DBIO[B] = DBIO(
    context =>
      run(context).map(f)(context.ec)
  )

  def flatMap[B](f: A => DBIO[B]): DBIO[B] = DBIO(
    context =>
      run(context).flatMap(f(_).run(context))(context.ec)
  )
}

object DBIOFactory {
  def fromDBSession[A](f: DBSession => A): DBIO[A] = DBIO(
    context => Future {
      f(context.dbSession)
    }(context.ec)
  )
}

object DBIO {
  def fromFuture[A](fa: Future[A]): DBIO[A] = DBIO(_ => fa)
}

class DBIORunner(ec: ExecutionContext) {
  def runLocalTx[A](dbio: DBIO[A]): Future[A] =
    DB.futureLocalTx(dbsession => dbio.run(DBIOContext(dbsession, ec)))(ec = ec)

  def runAutoCommit[A](dbio: DBIO[A]): Future[A] =
    DB.autoCommit(dbsession => dbio.run(DBIOContext(dbsession, ec)))

}