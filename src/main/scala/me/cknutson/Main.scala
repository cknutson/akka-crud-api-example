package me.cknutson

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import me.cknutson.service.{CommentService, PostService, UserService}

import scala.concurrent.ExecutionContext

object Main extends App with Routes {
  implicit val system: ActorSystem = ActorSystem("slick-example")
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val userService: ActorRef = system.actorOf(Props[UserService], "user-actor")
  val postService: ActorRef = system.actorOf(Props[PostService], "post-actor")
  val commentService: ActorRef = system.actorOf(Props[CommentService], "comment-actor")

  val config = ConfigFactory.load.getConfig("das-example")
  val address = config.getString("address")
  val port = config.getInt("port")
  Http().bindAndHandle(handler = logRequestResult("log")(routes), address, port)
  println(s"Now listening on port: $port")
}
