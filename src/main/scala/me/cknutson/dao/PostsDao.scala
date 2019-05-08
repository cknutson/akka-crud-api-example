package me.cknutson.dao

import me.cknutson.models.Post
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

trait PostsDao extends BaseDao {
  def getUserPosts(userId: Int): Future[Seq[Post]] = {
    (for {
      user <- usersTable.filter(_.id === userId)
      posts <- postsTable.filter(_.userId === user.id)
    } yield posts).result
  }
  def getByUserIdAndId(userId: Int, postId: Int): Future[Post] = {
    (for {
      user <- usersTable.filter(_.id === userId)
      post <- postsTable.filter(_.userId === postId)
    } yield post).result.head
  }
  def create(post: Post): Future[Int] = postsTable returning postsTable.map(_.id) += post
  def update(newPost: Post, postId: Int): Future[Int] = postsTable.filter(_.id === postId)
    .map(post => (post.title, post.content))
    .update(newPost.title, newPost.content)

  def delete(postId: Int): Future[Int] = postsTable.filter(_.id === postId).delete
}
