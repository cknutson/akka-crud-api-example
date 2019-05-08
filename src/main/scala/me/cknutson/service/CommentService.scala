package me.cknutson.service

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import me.cknutson.dao.CommentsDao
import me.cknutson.models.Comment

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object CommentService {
  def props: Props = Props[CommentService]

  case class GetPostCommentsReq(userId: Int, postId: Int)
  case class GetPostCommentsRes(comments: Seq[Comment])
  case class GetCommentByIdsReq(userId: Int, postId: Int, commentId: Int)
  case class GetCommentByIdsRes(comment: Comment)
  case class CreateCommentReq(comment: Comment)
  case class CreateCommentRes(id: Int)
  case class UpdateCommentReq(comment: Comment, commentId: Int)
  case class UpdateCommentRes(resCode: Int)
  case class DeleteUserReq(id: Int)
  case class DeleteUserRes(resCode: Int)
  case class ReportError(throwable: Throwable)
}

class CommentService extends CommentsDao with Actor with ActorLogging {
  import CommentService._
  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case GetPostCommentsReq(userId, postId) =>
      val requestor = sender()
      getPostComments(userId, postId) onComplete {
        case Success(comments) =>
          requestor ! GetPostCommentsRes(comments)
        case Failure(error) => handleError(requestor, error)
      }
    case GetCommentByIdsReq(userId, postId, commentId) =>
      val requestor = sender()
      getCommentByIds(userId, postId, commentId) onComplete {
        case Success(comments) => requestor ! GetCommentByIdsRes(comments)
        case Failure(error) => handleError(requestor, error)
      }
    case CreateCommentReq(comment) =>
      val requestor = sender()
      create(comment) onComplete {
        case Success(id) => requestor ! CreateCommentRes(id)
        case Failure(error) => handleError(requestor, error)
      }
    case UpdateCommentReq(newComment, id) =>
      val requestor = sender()
      update(newComment, id) onComplete {
        case Success(resCode) => requestor ! UpdateCommentRes(resCode)
        case Failure(error) => handleError(requestor, error)
      }

    case DeleteUserReq(id) =>
      val requestor = sender()
      delete(id) onComplete {
        case Success(resCode) => requestor ! DeleteUserRes(resCode)
        case Failure(error) => handleError(requestor, error)
      }
  }

  def handleError(requestor: ActorRef, error: Throwable): Unit = {
    requestor ! ReportError(error)
  }
}
