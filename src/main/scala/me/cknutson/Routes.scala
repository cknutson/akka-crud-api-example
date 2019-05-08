package me.cknutson

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import me.cknutson.api.{ApiErrorHandler, CommentsApi, PostsApi, UsersApi}

trait Routes extends ApiErrorHandler with UsersApi with PostsApi with CommentsApi {
  val routes: Route = pathPrefix("api") {
    usersApi ~
    postsApi ~
    commentsApi
  }
}
