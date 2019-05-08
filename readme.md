# Akka CRUD API Example

## Prerequisites
1. JDK 8+
2. SBT

## Introduction
This project is just a simple data access service, which can perform basic CRUD
operations on a small data model. It is written in Scala and it uses 3 Akka libraries
to help with architecture/boilerplate implementation. The libraries are: 
[Akka core](https://akka.io/docs/) for the Actor roles, 
[Akka http](https://doc.akka.io/docs/akka-http/current/) for the REST interface and 
[Slick](http://slick.lightbend.com/) for the database access/abstraction layer.

## Run
    $ sbt run

## Build
    $ sbt universal:packageBin
