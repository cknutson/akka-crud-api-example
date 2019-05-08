package me.cknutson.api

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import me.cknutson.models.{JsonMappings, User, Users}
import me.cknutson.service.UserService._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

trait UsersApi extends JsonMappings {
  implicit def system: ActorSystem
  def userService: ActorRef
  implicit lazy val usersTimeout: Timeout = Timeout(5 seconds)

  val usersApi: Route = {
    pathPrefix("users") {
      get {
        pathEndOrSingleSlash {
          val res = (userService ? GetAllUsersReq()).map[ToResponseMarshallable] {
            case GetAllUsersRes(users) => Users(users)
            case ReportError(error) => s"There was an error: $error"
          }
          complete(res)
        } ~
        path(IntNumber) { id =>
          val res = (userService ? GetUserByIdReq(id)).map[ToResponseMarshallable] {
            case GetUserByIdRes(user) => user
            case ReportError(error) => s"There was an error: $error"
          }
          complete(res)
        }
      } ~
      post {
        entity(as[User]) { user =>
          val res = (userService ? CreateUserReq(user)).map[ToResponseMarshallable] {
            case CreateUserRes(id) => id.toString
            case ReportError(error) => s"There was an error: $error"
          }
          complete(res)
        }
      } ~
      put {
        path(IntNumber) { id =>
          entity(as[User]) { user =>
            val res = (userService ? UpdateUserReq(user, id)).map[ToResponseMarshallable] {
              case UpdateUserRes(resCode) => resCode.toString
              case ReportError(error) => s"There was an error: $error"
            }
            complete(res)
          }
        }
      } ~
      delete {
        path(IntNumber) { id =>
          val res = (userService ? DeleteUserReq(id)).map[ToResponseMarshallable] {
            case DeleteUserRes(resCode) => resCode.toString
            case ReportError(error) => s"There was an error: $error"
          }
          complete(res)
        }
      }
    }
  }
}
