package me.cknutson.service

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import me.cknutson.dao.UsersDao
import me.cknutson.models.User

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object UserService {
  def props: Props = Props[UserService]

  case class GetAllUsersReq()
  case class GetAllUsersRes(users: Seq[User])
  case class GetUserByIdReq(id: Int)
  case class GetUserByIdRes(user: User)
  case class CreateUserReq(user: User)
  case class CreateUserRes(id: Int)
  case class UpdateUserReq(user: User, userId: Int)
  case class UpdateUserRes(resCode: Int)
  case class DeleteUserReq(id: Int)
  case class DeleteUserRes(resCode: Int)
  case class ReportError(throwable: Throwable)
}

class UserService extends UsersDao with Actor with ActorLogging {
  import UserService._
  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case GetAllUsersReq() =>
      val requestor = sender()
      getAll onComplete {
        case Success(users) =>
          requestor ! GetAllUsersRes(users)
        case Failure(error) => handleError(requestor, error)
      }
    case GetUserByIdReq(id) =>
      val requestor = sender()
      getById(id) onComplete {
        case Success(user) => requestor ! GetUserByIdRes(user)
        case Failure(error) => handleError(requestor, error)
      }
    case CreateUserReq(user) =>
      val requestor = sender()
      create(user) onComplete {
        case Success(id) => requestor ! CreateUserRes(id)
        case Failure(error) => handleError(requestor, error)
      }
    case UpdateUserReq(newUser, id) =>
      val requestor = sender()
      update(newUser, id) onComplete {
        case Success(resCode) => requestor ! UpdateUserRes(resCode)
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
