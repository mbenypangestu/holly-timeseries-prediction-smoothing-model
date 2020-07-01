package services

import database.Mongo._
import helper.Helpers._

import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts.{orderBy, ascending, descending}
import org.mongodb.scala.model.Aggregates._

object TemporalDataService {
  def findIdByMonthYear(
      hotel_id: String,
      predictionMonth: Int,
      predictionYear: Int
  ): Seq[Document] = {
    val collection: MongoCollection[Document] =
      db.getCollection("temporal_data")
    collection
      .find(
        and(
          equal("month", predictionMonth),
          equal("year", predictionYear),
          equal("hotel_id", hotel_id)
        )
      )
      .first()
      .results()
  }

  def findByHotelId(
      hotel_id: String,
      baseline: Int,
      predictionMonth: Int,
      predictionYear: Int,
      object_id: ObjectId
  ): Seq[Document] = {
    val collection: MongoCollection[Document] =
      db.getCollection("temporal_data")
    collection
      .find(and(equal("hotel_id", hotel_id), lt("_id", object_id)))
      .limit(baseline)
      .sort(descending("year", "month"))
      .results()
  }
}
