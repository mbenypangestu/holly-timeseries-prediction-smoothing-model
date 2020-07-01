import java.text.SimpleDateFormat
import java.util.Calendar
import org.mongodb.scala.bson.ObjectId

import org.mongodb.scala.bson.collection.immutable.Document
import services.LocationService.findIndonesianLocations
import services.HotelService.findByLocationId
import services.TemporalDataService._
import services.PredictionService._
import org.mongodb.scala.model.Sorts.{orderBy, ascending, descending}

import util.control.Breaks._

object Main extends App {
  val currentDateFormat = new SimpleDateFormat("d-M-y hh:mm:ss")
  val now = currentDateFormat.format(Calendar.getInstance().getTime)

  val baseline = 10
  var predictionMonth = 8
  var predictionYear = 2020

  println("[ " + now + "] Start prediction process...")

  val locations = findIndonesianLocations()
  breakable {
    locations.foreach(location => {
      println("[ " + now + "] " + location("name").asString().getValue)

      val hotels = findByLocationId(location("location_id").asInt32().getValue)

      breakable {
        hotels.foreach(hotel => {

          println("[ " + now + "] " + hotel("location_id").asString().getValue)

          var beforePredictionMonth = predictionMonth - 1
          var beforePredictionYear = predictionYear
          if (beforePredictionMonth == 1) {
            beforePredictionMonth = 12
            beforePredictionYear = beforePredictionYear - 1
          }

          val temporalId = findIdByMonthYear(
            hotel("location_id").asString().getValue,
            beforePredictionMonth,
            beforePredictionYear
          )
          if (temporalId.nonEmpty) {
            print(temporalId(0)("_id").asObjectId().getValue)
            val temporal =
              findByHotelId(
                hotel("location_id").asString().getValue,
                baseline,
                beforePredictionMonth,
                beforePredictionYear,
                temporalId(0)("_id").asObjectId().getValue
              ).reverse

            var months = Seq[Int]()
            var years = Seq[Int]()
            var rooms = Seq[Double]()
            var values = Seq[Double]()
            var sleep_qualities = Seq[Double]()
            var locs = Seq[Double]()
            var cleanliness = Seq[Double]()
            var services = Seq[Double]()
            var wordnets = Seq[Double]()
            var vaders = Seq[Double]()

            temporal.foreach({
              t =>
                val month = t("month").asInt32().getValue
                val year = t("year").asInt32().getValue
                val room = t("rating_rooms").asDouble().getValue
                val value = t("rating_value").asDouble().getValue
                val sleep_quality =
                  t("rating_sleep_quality").asDouble().getValue
                val loc = t("rating_location").asDouble().getValue
                val clean = t("rating_cleanliness").asDouble().getValue
                val service = t("rating_service").asDouble().getValue
                val wordnet = t("wordnet_score").asDouble().getValue
                val vader = t("vader_compound_score").asDouble().getValue

                months = months :+ month
                years = years :+ year
                rooms = rooms :+ room
                values = values :+ value
                sleep_qualities = sleep_qualities :+ sleep_quality
                locs = locs :+ loc
                cleanliness = cleanliness :+ clean
                services = services :+ service
                wordnets = wordnets :+ wordnet
                vaders = vaders :+ vader

                //   println(s"service (${month}/${year}) => ${vaders}")

                if (temporal.nonEmpty) {
                  // var nextMonth = months.last + 1
                  // var nextYear = years.last
                  // if (months.last == 12) {
                  //   nextMonth = 1
                  //   nextYear = years.last
                  // }

                  val roomPrediction: Double =
                    getSmoothingPrediction(rooms, 0.8)
                  val valuePrediction: Double =
                    getSmoothingPrediction(values, 0.8)
                  val sleepQualityPrediction: Double =
                    getSmoothingPrediction(sleep_qualities, 0.8)
                  val locationPrediction: Double =
                    getSmoothingPrediction(locs, 0.8)
                  val cleanlinessPrediction: Double =
                    getSmoothingPrediction(cleanliness, 0.8)
                  val servicePrediction: Double =
                    getSmoothingPrediction(services, 0.8)
                  val vaderPrediction: Double =
                    getSmoothingPrediction(vaders, 0.8)
                  val wordnetPrediction: Double =
                    getSmoothingPrediction(wordnets, 0.8)

                  val document: Document = Document(
                    "location_id" -> location("location_id").asInt32().getValue,
                    "location" -> location,
                    "hotel_id" -> hotel("location_id").asString().getValue,
                    "hotel" -> hotel,
                    "month" -> predictionMonth,
                    "year" -> predictionYear,
                    "rating_rooms" -> roomPrediction,
                    "rating_value" -> valuePrediction,
                    "rating_sleep_quality" -> sleepQualityPrediction,
                    "rating_location" -> locationPrediction,
                    "rating_cleanliness" -> cleanlinessPrediction,
                    "rating_service" -> servicePrediction,
                    "vader" -> vaderPrediction,
                    "wordnet" -> wordnetPrediction
                  )
                  savePredictionResult(
                    hotel("location_id").asString().getValue,
                    predictionMonth,
                    predictionYear,
                    document
                  )
                }
            })
          }

        })
        // break()
      }

    })
    // break()
  }

}
