package me.cknutson.dao

import me.cknutson.models.{CommentsTable, PostsTable, UsersTable}
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery
import slick.sql.{FixedSqlStreamingAction, SqlAction}

import scala.concurrent.Future

trait BaseDao {
  val dbConfig = "mysql"
  lazy val db = Database.forConfig(dbConfig)

  val usersTable = TableQuery[UsersTable]
  val postsTable = TableQuery[PostsTable]
  val commentsTable = TableQuery[CommentsTable]

  protected implicit def executeFromDb[A](action: SqlAction[A, NoStream, _ <: slick.dbio.Effect]): Future[A] = {
    db.run(action)
  }
  protected implicit def executeReadStreamFromDb[A](action: FixedSqlStreamingAction[Seq[A], A, _ <: slick.dbio.Effect]): Future[Seq[A]] = {
    db.run(action)
  }
}
