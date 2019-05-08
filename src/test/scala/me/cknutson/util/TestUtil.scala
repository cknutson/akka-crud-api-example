package me.cknutson.util

import me.cknutson.models.User

object TestUtil {
  def getSingleUser: User = {
    getUsers.head
  }

  def getNewUser: User = {
    User(Some(6), "TestUser", "test-password", 31, 0)
  }

  def getUsers: Seq[User] = {
    Vector(
      User(Some(1), "Casey", "my", 35, 1),
      User(Some(2), "Steph", "super", 16, 0),
      User(Some(3), "Frank", "secret", 31, 1),
      User(Some(4), "Jeff", "password", 35, 1),
      User(Some(5), "Abe", "yeah!", 32, 1)
    )
  }
}