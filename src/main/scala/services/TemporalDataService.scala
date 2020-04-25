package services

import database.Mongo._
import helper.Helpers._

import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts.{orderBy,ascending}

object TemporalDataService {
  def findByHotelId(hotel_id : ObjectId): Seq[Document] = {
    val collection : MongoCollection[Document] = db.getCollection("temporal_data")
    collection.find(equal("hotel.id", hotel_id)).sort(ascending("year", "month")).results()
  }
}
