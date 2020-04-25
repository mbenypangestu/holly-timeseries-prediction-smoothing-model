package services

import database.Mongo._
import helper.Helpers._

import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document

import org.mongodb.scala.model.Filters._

object HotelService {

  def findAll(): Seq[Document] ={
    val collection:MongoCollection[Document] = db.getCollection("hotel")

    collection.find().results()
  }

  def findByLocationId(location_id: ObjectId): Seq[Document] ={
    val collection:MongoCollection[Document] = db.getCollection("hotel")

    collection.find(equal("location._id", location_id)).results()
  }
}
