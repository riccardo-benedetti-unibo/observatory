package core

import com.sksamuel.scrimage.{Image, Pixel}
import model.{Location, Temperature, Color}

object Visualization {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {
    temperatures.find(_._1 == location) match {
      case Some((_, temp)) => temp
      case _ => {
        val idws = temperatures.map(x => (greatCircleDistExpanded(x._1, location), x._2))
        idws.foldLeft(0d)((curr, next) => curr + getIDW(next._1) * next._2) / 
          idws.foldLeft(0d)((curr, next) => curr + getIDW(next._1))
      }
    }
  }
  
  final val p = 3
  final val r = 6371d
  
  def getIDW(distance: Double): Double = 1 / (scala.math.pow(distance, p))
  
  def greatCircleDistExpanded(l1: Location, l2: Location): Double =
    l1 match {
      case Location(l2.lat, l2.lon) => 0d
      case _ => r * haversine(l1, l2)
    }
  
  // https://www.movable-type.co.uk/scripts/latlong.html
  def haversine(l1: Location, l2: Location): Double = {
    val dlat = toRadians(l2.lat - l1.lat)
    val dlon = toRadians(l2.lon- l1.lon)
    val a = (scala.math.sin(dlat/2) * scala.math.sin(dlat/2)) + scala.math.cos(toRadians(l1.lat)) *
              scala.math.cos(toRadians(l2.lat)) * (scala.math.sin(dlon/2) * scala.math.sin(dlon/2))
    val c = 2 * scala.math.atan2(scala.math.sqrt(a), scala.math.sqrt(1-a))
    c
  }
    
  def toRadians(degrees: Double): Double = degrees * scala.math.Pi / 180

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    points.find(_._1 == value) match {
      case Some((_, c)) => c
      case None => {
        val partList = points.toList.sortBy(_._1).partition(_._1 <  value)
        if(partList._1.isEmpty) partList._2.head._2
        else if(partList._2.isEmpty) partList._1.last._2
        else {
          val (lbT, ubT, lbC, ubC) = (partList._1.last._1, partList._2.head._1,
              partList._1.last._2, partList._2.head._2)
          def getInterpolatedChannel(lbCC: Int, ubCC: Int): Int = {
            scala.math.round(lbCC + (value - lbT) * (ubCC - lbCC) / (ubT - lbT)).toInt
          }
          Color(
            getInterpolatedChannel(lbC.red, ubC.red),
            getInterpolatedChannel(lbC.green, ubC.green),
            getInterpolatedChannel(lbC.blue, ubC.blue)
          )
        } 
      }
    }
  }
  
  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    val pixels = (0 until (model.mapWidth * model.mapHeight)).par.map(x => {
      val loc = toLocation(x)
      val predTemp = predictTemperature(temperatures, loc)
      val color = interpolateColor(colors, predTemp)
      Pixel.apply(color.red, color.green, color.blue, model.mapAlpha)
    }).seq.toArray
    Image(model.mapWidth, model.mapHeight, pixels)
  }
  
  def toLocation(index: Int): Location = Location(90 - index / model.mapWidth, index % model.mapWidth - 180)
}