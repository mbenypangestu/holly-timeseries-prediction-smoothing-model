package services

import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.UpdateOptions

import scala.math.pow

import database.Mongo._
import helper.Helpers._

object PredictionService {
  def getSmoothingPrediction(data: Seq[Double], alpha: Double): Double = {
    var result: Double = 0
    val n: Int = data.length

    data.zipWithIndex foreach {
      case (d, i) =>
        val t = n - 1 - i

        if (i == 0) {
          result += pow((1 - alpha), t) * d
        } else {
          result += alpha * pow((1 - alpha), t) * d
        }
    }
    result
  }

  def savePredictionResult(
      hotel_id: String,
      predictionMonth: Int,
      predictionYear: Int,
      document: Document
  ): Unit = {
    val collection = db.getCollection("prediction")
    val prediction = collection
      .find(
        and(
          equal("month", predictionMonth),
          equal("year", predictionYear),
          equal("hotel_id", hotel_id)
        )
      )
      .headResult()

    if (prediction != null) {
      collection
        .replaceOne(
          equal("_id", prediction("_id").asObjectId().getValue),
          document,
          new UpdateOptions().upsert(true)
        )
        .printResults()
    } else {
      collection.insertOne(document).printHeadResult()
    }
  }
}
