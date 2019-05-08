package me.cknutson.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonMappings extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._
  implicit val userFormat: RootJsonFormat[User] = jsonFormat5(User)
  implicit val usersFormat: RootJsonFormat[Users] = jsonFormat1(Users)
  implicit val postFormat: RootJsonFormat[Post] = jsonFormat4(Post)
  implicit val postsFormat: RootJsonFormat[Posts] = jsonFormat1(Posts)
  implicit val commentFormat: RootJsonFormat[Comment] = jsonFormat4(Comment)
  implicit val commentsFormat: RootJsonFormat[Comments] = jsonFormat1(Comments)
}