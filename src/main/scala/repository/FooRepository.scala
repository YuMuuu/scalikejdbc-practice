package repository

import io._
import scalikejdbc._

case class Foo(
                id: String,
                name: String
              )

object Foo {
  def from(rs: WrappedResultSet): Foo = Foo(
    id = rs.string("id"),
    name = rs.string("name")
  )
}

object FooRepository {

  def create(foo: Foo): DBIO[Unit] =
    DBIOFactory.fromDBSession {
      implicit session => {
        sql"""INSERT INTO foos (id, name) VALUES (${foo.id}, ${foo.name})"""
          .update.apply()
      }
    }

  def fetch(id: String): DBIO[Option[Foo]] = DBIOFactory.fromDBSession(
    implicit session =>
      sql"""SELECT id, name FROM Foos
           |WHERE id = ${id}""".stripMargin
        .map(Foo.from).single.apply()

  )

  def update(foo: Foo): DBIO[Unit] = DBIOFactory.fromDBSession(
    implicit session =>
      sql"""UPDATE Foo SET name=${foo.name} WHERE id = ${foo.id}"""
        .update.apply()
  )
}
