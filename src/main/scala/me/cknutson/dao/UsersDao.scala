package me.cknutson.dao

import me.cknutson.models.User
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

trait UsersDao extends BaseDao {
  def getAll: Future[Seq[User]] = usersTable.result
  def getById(id: Int): Future[User] = usersTable.filter(_.id === id).result.head
  def create(user: User): Future[Int] = usersTable returning usersTable.map(_.id) += user
  def update(newUser: User, userId: Int): Future[Int] = usersTable.filter(_.id === userId)
    .map(user => (user.username, user.password, user.gender, user.age))
    .update(newUser.userName, newUser.password, newUser.gender, newUser.age)

  def delete(userId: Int): Future[Int] = usersTable.filter(_.id === userId).delete
}
