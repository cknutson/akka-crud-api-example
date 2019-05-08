package me.cknutson.api

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ContentTypes, MessageEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestProbe
import me.cknutson.models.Users
import me.cknutson.service.UserService._
import me.cknutson.util.TestUtil
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class UsersApiSpec extends WordSpec
  with Matchers with ScalatestRouteTest with ScalaFutures with UsersApi {
  val probe = TestProbe()
  val userService: ActorRef = probe.ref

  "UserApi" should {
    "get all users (GET /users)" in {
      val users = TestUtil.getUsers

      val result = Get("/users") ~> usersApi ~> runRoute
      probe.expectMsg(GetAllUsersReq())
      probe.reply(GetAllUsersRes(users))
      check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        entityAs[String] shouldEqual s"""${Users(users).toJson}"""
      }(result)
    }

    "get a user by ID (GET /users/<id>)" in {
      val user = TestUtil.getSingleUser
      val result = Get(s"/users/${user.id.get}") ~> usersApi ~> runRoute
      probe.expectMsg(GetUserByIdReq(user.id.get))
      probe.reply(GetUserByIdRes(user))
      check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        entityAs[String] shouldEqual s"""${user.toJson}"""
      }(result)
    }

    "create a new user (POST /users)" in {
      val newUser = TestUtil.getNewUser
      val entity = Marshal(newUser).to[MessageEntity].futureValue
      val result = Post("/users").withEntity(entity) ~> usersApi ~> runRoute
      probe.expectMsg(CreateUserReq(newUser))
      probe.reply(CreateUserRes(newUser.id.get))
      check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`text/plain(UTF-8)`
        entityAs[String] shouldEqual newUser.id.get.toString
      }(result)
    }

    "update a user (PUT /users/<id>)" in {
      val updatedUser = TestUtil.getSingleUser.copy(age = 99)
      val entity = Marshal(updatedUser).to[MessageEntity].futureValue
      val result = Put(s"/users/${updatedUser.id.get}").withEntity(entity) ~> usersApi ~> runRoute
      probe.expectMsg(UpdateUserReq(updatedUser, updatedUser.id.get))
      probe.reply(UpdateUserRes(1))
      check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`text/plain(UTF-8)`
        entityAs[String] shouldEqual 1.toString
      }(result)
    }

    "delete a user (DELETE /users/<id>)" in {
      val id = TestUtil.getSingleUser.id.get
      val result = Delete(s"/users/$id") ~> usersApi ~> runRoute
      probe.expectMsg(DeleteUserReq(id))
      probe.reply(DeleteUserRes(1))
      check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`text/plain(UTF-8)`
        entityAs[String] shouldEqual 1.toString
      }(result)
    }
  }
}