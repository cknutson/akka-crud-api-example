package me.cknutson.dao

import me.cknutson.models.User
import me.cknutson.util.TestUtil
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class UsersDaoSpec extends WordSpec
  with Matchers with BeforeAndAfter with ScalaFutures with UsersDao {
  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  override val dbConfig = "h2"
  val users: Seq[User] = TestUtil.getUsers
  Await.result(db.run(DBIO.seq(
    usersTable.schema.create,
    usersTable ++= users
  )), Duration.Inf)

  "UserDao" should {
    "get all values" in {
      val results = getAll.futureValue
      assert(results.size === 5)
    }

    "get a user by ID" in {
      val results = getById(1).futureValue
      assert(results === TestUtil.getSingleUser)
    }

    "create a new User" in {
      val newUser = TestUtil.getNewUser
      val id = create(newUser).futureValue
      val users = db.run(usersTable.result).futureValue
      val dbNewUser = db.run(usersTable.filter(_.id === 6).result.head).futureValue
      assert(id === newUser.id.get)
      assert(users.size === 6)
      assert(dbNewUser === newUser)
    }

    "update a user" in {
      val updatedUser = TestUtil.getNewUser.copy(age = 99)
      val resCode = update(updatedUser, updatedUser.id.get).futureValue
      val dbUpdatedUser = db.run(usersTable.filter(_.id === 6).result.head).futureValue
      assert(resCode === 1)
      assert(dbUpdatedUser === updatedUser)
    }

    "delete a user" in {
      Await.result(usersTable.filter(_.id === 6).delete, Duration.Inf)
      val users = db.run(usersTable.result).futureValue
      assert(users.size === 5)
    }
  }
}