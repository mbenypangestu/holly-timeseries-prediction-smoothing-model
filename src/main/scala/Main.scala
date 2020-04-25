import java.text.SimpleDateFormat
import java.util.Calendar

import services.LocationService.findIndonesianLocations
import services.HotelService.findByLocationId

object Main extends App {
  val currentDateFormat = new SimpleDateFormat("d-M-y hh:mm:ss")
  val now = currentDateFormat.format(Calendar.getInstance().getTime)

  println("[ " + now + "] Start prediction process ...")

  val locations = findIndonesianLocations()
  locations.foreach(location => {
    println("[ " + now + "] "+ location("name").asString().getValue)

    val hotels = findByLocationId(location("_id").asObjectId().getValue)
    hotels.foreach(hotel => {
      println("[ " + now + "] "+ hotel("name").asString().getValue)
    })
  })
}