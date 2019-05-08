package me.cknutson.api

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import me.cknutson.models.{JsonMappings, Post, Posts}
import me.cknutson.service.PostService._
import me.cknutson.service.UserService.ReportError

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

trait PostsApi extends JsonMappings {
  implicit def system: ActorSystem
  def postService: ActorRef
  implicit lazy val postsTimeout: Timeout = Timeout(5 seconds)

  val postUsersPath = "users"
  val postsPath = "posts"
  val postsApi: Route = {
    get {
      path(postUsersPath / IntNumber / postsPath) { userId =>
        val res = (postService ? GetUserPostsReq(userId)).map[ToResponseMarshallable] {
          case GetUserPostsRes(posts) => Posts(posts)
          case ReportError(error) => s"There was an error: $error"
        }
        complete(res)
      } ~
      path(postUsersPath / IntNumber / postsPath / IntNumber) { (userId, postId) =>
        val res = (postService ? GetByUserIdAndIdReq(userId, postId)).map[ToResponseMarshallable] {
          case GetByUserIdAndIdRes(post) => post
          case ReportError(error) => s"There was an error: $error"
        }
        complete(res)
      }
    } ~
    post {
      path(postsPath) {
        entity(as[Post]) { post =>
          val res = (postService ? CreatePostReq(post)).map[ToResponseMarshallable] {
            case CreatePostRes(id) => id.toString
            case ReportError(error) => s"There was an error: $error"
          }
          complete(res)
        }
      }
    } ~
    put {
      path(postsPath / IntNumber) { id =>
        entity(as[Post]) { post =>
          val res = (postService ? UpdatePostReq(post, id)).map[ToResponseMarshallable] {
            case UpdatePostRes(resCode) => resCode.toString
            case ReportError(error) => s"There was an error: $error"
          }
          complete(res)
        }
      }
    } ~
    delete {
      path(postsPath / IntNumber) { id =>
        val res = (postService ? DeletePostReq(id)).map[ToResponseMarshallable] {
          case DeletePostRes(resCode) => resCode.toString
          case ReportError(error) => s"There was an error: $error"
        }
        complete(res)
      }
    }
  }
}
