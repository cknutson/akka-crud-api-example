package me.cknutson.service

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import me.cknutson.dao.PostsDao
import me.cknutson.models.Post

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object PostService {
  def props: Props = Props[PostService]

  case class GetUserPostsReq(userId: Int)
  case class GetUserPostsRes(posts: Seq[Post])
  case class GetByUserIdAndIdReq(userId: Int, postId: Int)
  case class GetByUserIdAndIdRes(post: Post)
  case class CreatePostReq(post: Post)
  case class CreatePostRes(id: Int)
  case class UpdatePostReq(post: Post, postId: Int)
  case class UpdatePostRes(resCode: Int)
  case class DeletePostReq(id: Int)
  case class DeletePostRes(resCode: Int)
  case class ReportError(throwable: Throwable)
}

class PostService extends PostsDao with Actor with ActorLogging {
  import PostService._
  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case GetUserPostsReq(userId) =>
      val requestor = sender()
      getUserPosts(userId) onComplete {
        case Success(posts) =>
          requestor ! GetUserPostsRes(posts)
        case Failure(error) => handleError(requestor, error)
      }
    case GetByUserIdAndIdReq(userId, postId) =>
      val requestor = sender()
      getByUserIdAndId(userId, postId) onComplete {
        case Success(post) => requestor ! GetByUserIdAndIdRes(post)
        case Failure(error) => handleError(requestor, error)
      }
    case CreatePostReq(post) =>
      val requestor = sender()
      create(post) onComplete {
        case Success(id) => requestor ! CreatePostRes(id)
        case Failure(error) => handleError(requestor, error)
      }
    case UpdatePostReq(post, id) =>
      val requestor = sender()
      update(post, id) onComplete {
        case Success(resCode) => requestor ! UpdatePostRes(resCode)
        case Failure(error) => handleError(requestor, error)
      }
    case DeletePostReq(id) =>
      val requestor = sender()
      delete(id) onComplete {
        case Success(resCode) => requestor ! DeletePostRes(resCode)
        case Failure(error) => handleError(requestor, error)
      }
  }

  def handleError(requestor: ActorRef, error: Throwable): Unit = {
    requestor ! ReportError(error)
  }
}
