package database

import org.mongodb.scala.{MongoClient, MongoDatabase}

object Mongo {
  val mongoClient: MongoClient = MongoClient()
  val db: MongoDatabase = mongoClient.getDatabase("holly_dev")
}
