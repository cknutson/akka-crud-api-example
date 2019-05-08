package me.cknutson.models

import slick.jdbc.MySQLProfile.api._

case class Post(id: Option[Int], userId: Int, title: String, content: String)
case class Posts(posts: Seq[Post])

class PostsTable(tag: Tag) extends Table[Post](tag, "posts") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Int]("user_id")
  def title = column[String]("title")
  def content = column[String]("content")
  def * = (id.?, userId, title, content) <> ((Post.apply _).tupled, Post.unapply)

  def author = foreignKey("user_fk", userId, TableQuery[UsersTable])(_.id)
}
