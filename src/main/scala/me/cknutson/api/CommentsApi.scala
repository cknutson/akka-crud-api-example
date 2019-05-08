package me.cknutson.api

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import me.cknutson.models.{Comment, Comments, JsonMappings}
import me.cknutson.service.CommentService._
import me.cknutson.service.UserService.ReportError

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

trait CommentsApi extends JsonMappings {
  implicit def system: ActorSystem
  def commentService: ActorRef
  implicit lazy val commentsTimeout: Timeout = Timeout(5 seconds)

  val commentUsersPath = "users"
  val commentPostsPath = "posts"
  val commentsPath = "comments"
  val commentsApi: Route = {
    get {
      path(commentUsersPath / IntNumber / commentPostsPath / IntNumber / commentsPath) { (userId, postId) =>
        val res = (commentService ? GetPostCommentsReq(userId, postId)).map[ToResponseMarshallable] {
          case GetPostCommentsRes(comments) => Comments(comments)
          case ReportError(error) => s"There was an error: $error"
        }
        complete(res)
      } ~
      path(commentUsersPath / IntNumber / commentPostsPath / IntNumber / commentsPath / IntNumber) { (userId, postId, commentId) =>
        val res = (commentService ? GetCommentByIdsReq(userId, postId, commentId)).map[ToResponseMarshallable] {
          case GetCommentByIdsRes(comment) => comment
          case ReportError(error) => s"There was an error: $error"
        }
        complete(res)
      }
    } ~
    post {
      path(commentsPath) {
        entity(as[Comment]) { comment =>
          val res = (commentService ? CreateCommentReq(comment)).map[ToResponseMarshallable] {
            case CreateCommentRes(id) => id.toString
            case ReportError(error) => s"There was an error: $error"
          }
          complete(res)
        }
      }
    } ~
    put {
      path(commentsPath / IntNumber) { id =>
        entity(as[Comment]) { comment =>
          val res = (commentService ? UpdateCommentReq(comment, id)).map[ToResponseMarshallable] {
            case UpdateCommentRes(resCode) => resCode.toString
            case ReportError(error) => s"There was an error: $error"
          }
          complete(res)
        }
      }
    } ~
    delete {
      path(commentsPath / IntNumber) { id =>
        val res = (commentService ? DeleteUserReq(id)).map[ToResponseMarshallable] {
          case DeleteUserRes(resCode) => resCode.toString
            case ReportError(error) => s"There was an error: $error"
        }
        complete(res)
      }
    }
  }
}