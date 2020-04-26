import java.text.SimpleDateFormat
import java.util.Calendar

import org.mongodb.scala.bson.collection.immutable.Document
import services.LocationService.findIndonesianLocations
import services.HotelService.findByLocationId
import services.TemporalDataService.findByHotelId
import services.PredictionService._

import util.control.Breaks._

object Main extends App {
  val currentDateFormat = new SimpleDateFormat("d-M-y hh:mm:ss")
  val now = currentDateFormat.format(Calendar.getInstance().getTime)

  println("[ " + now + "] Start prediction process ...")

  val locations = findIndonesianLocations()
  breakable {
    locations.foreach(location => {
      println("[ " + now + "] "+ location("name").asString().getValue)

      val hotels = findByLocationId(location("_id").asObjectId().getValue)
      hotels.foreach(hotel => {
//        println("[ " + now + "] "+ hotel("name").asString().getValue)
        val temporal = findByHotelId(hotel("_id").asObjectId().getValue)

        var months = Seq[Int]()
        var years = Seq[Int]()
        var rooms = Seq[Double]()
        var values = Seq[Double]()
        var sleep_qualities = Seq[Double]()
        var locations = Seq[Double]()
        var cleanliness = Seq[Double]()
        var services = Seq[Double]()
        var wordnets = Seq[Double]()
        var vaders = Seq[Double]()

        temporal.foreach ({t =>
          val month = t("month").asInt32().getValue
          val year = t("year").asInt32().getValue
          val room = t("rating_rooms").asDouble().getValue
          val value = t("rating_value").asDouble().getValue
          val sleep_quality = t("rating_sleep_quality").asDouble().getValue
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
          locations = locations :+ loc
          cleanliness = cleanliness :+ clean
          services = services :+ service
          wordnets = wordnets :+ wordnet
          vaders = vaders :+ vader

//          println(s"service (${month}/${year}) => ${service}")

          if (temporal.nonEmpty) {
            var nextMonth = months.last + 1
            var nextYear = years.last
            if (months.last == 12) {
              nextMonth = 1
              nextYear = years.last
            }

            val roomPrediction = 0.8
            val valuePrediction = 0.8
            val sleepQualityPrediction = 0.8
            val locationPrediction = 0.8
            val cleanlinessPrediction = 0.8
            val servicePrediction = 0.8
            val vaderPrediction = 0.8
            val wordnetPrediction = 0.8

            val document = Document(
              "location_id" -> location("location_id").asInt32().getValue,
              "location" -> location,
              "hotel_id" -> hotel("location_id").asString().getValue,
              "hotel" -> hotel,
              "month" -> nextMonth,
              "year" -> nextYear,
              "rating_rooms" -> roomPrediction,
              "rating_value" -> valuePrediction,
              "rating_sleep_quality" -> sleepQualityPrediction,
              "rating_location" -> locationPrediction,
              "rating_cleanliness" -> cleanlinessPrediction,
              "rating_service" -> servicePrediction,
              "vader" -> vaderPrediction,
              "wordnet" -> wordnetPrediction
            )
//            println(document)
//            savePredictionResult(hotel("_id").asObjectId().getValue, document)
          }
        })
      })
      break()
    })
  }
}