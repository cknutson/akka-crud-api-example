package me.cknutson.dao

import me.cknutson.models.Comment
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Future

trait CommentsDao extends BaseDao {
  def getPostComments(userId: Int, postId: Int): Future[Seq[Comment]] = {
    (for {
      user <-usersTable.filter(_.id === userId)
      post <-postsTable.filter(_.id === postId)
      comment <- commentsTable.filter(comment => comment.postId === postId && comment.userId === userId)
    } yield comment).result
  }

  def getCommentByIds(userId: Int, postId: Int, commentId: Int): Future[Comment] = {
    (for{
      user <- usersTable.filter(_.id === userId)
      post <- postsTable.filter(_.id === postId)
      comment <- commentsTable.filter(comment => comment.id === commentId)
    } yield comment).result.head
  }
  def create(comment: Comment): Future[Int] = commentsTable returning commentsTable.map(_.id) += comment
  def update(newComment: Comment, commentId: Int): Future[Int] = commentsTable.filter(_.id === commentId)
    .map(comment => comment.content)
    .update(newComment.content)

  def delete(commentId: Int): Future[Int] = commentsTable.filter(_.id === commentId).delete
}
