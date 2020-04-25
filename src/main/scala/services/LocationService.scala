package services

import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.bson.collection.immutable.Document
import database.Mongo._
import helper.Helpers._

object LocationService {
  def findIndonesianLocations():Seq[Document]={
    val cities = Seq(
      "Banda Aceh",
      "Medan",
      "Padang",
      "Pekanbaru",
      "Palembang",
      "Bengkulu",
      "Bandar Lampung",
      "Pangkal Pinang",
      "Tanjung Pinang",
      "Jakarta",
      "Bandung",
      "Semarang",
      "Yogyakarta Region",
      "Surabaya",
      "Serang",
      "Denpasar",
      "Mataram",
      "Kupang",
      "Pontianak",
      "Banjarmasin",
      "Samarinda",
      "Manado",
      "Palu",
      "Makassar",
      "Kendari",
      "Gorontalo",
      "Mamuju",
      "Ambon",
      "Jayapura",
      "Manokwari",
      "Malang",
      "Sidoarjo"
    )

    val collection:MongoCollection[Document] = db.getCollection("location")

    var locations = Seq[Document]()

    cities.foreach{city =>
      val location:Document = collection.find(equal("name",city)).headResult()
      locations = locations :+ location
    }

    locations
  }
}
