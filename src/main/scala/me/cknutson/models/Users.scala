package me.cknutson.models

import slick.jdbc.MySQLProfile.api._

case class User(id: Option[Int], userName: String, password: String, age: Int, gender: Int)
case class Users(users: Seq[User])

class UsersTable(tag: Tag) extends Table[User](tag, "users") {
  def id       = column[Int]   ("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def password = column[String]("password")
  def age      = column[Int]   ("age")
  def gender   = column[Int]   ("gender")
  def * = (id.?, username, password, age, gender) <> ((User.apply _).tupled, User.unapply)
}