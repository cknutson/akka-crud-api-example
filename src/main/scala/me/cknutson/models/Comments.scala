package me.cknutson.models

import slick.jdbc.MySQLProfile.api._

case class Comment(id: Option[Int], userId: Int, postId: Int, content: String)
case class Comments(comments: Seq[Comment])

class CommentsTable(tag: Tag) extends Table[Comment](tag, "comments") {
  def id      = column[Int]   ("id", O.PrimaryKey, O.AutoInc)
  def userId  = column[Int]   ("user_id")
  def postId  = column[Int]   ("post_id")
  def content = column[String]("content")
  def * = (id.?, userId, postId, content) <> ((Comment.apply _).tupled, Comment.unapply)

  def author = foreignKey("comment_user_fk", userId, TableQuery[UsersTable])(_.id)
  def post = foreignKey("comment_post_fk", postId, TableQuery[PostsTable])(_.id)
}