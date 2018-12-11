package core

import com.sksamuel.scrimage.{Image, Pixel}
import model.{Tile, Location, Temperature, Year, Color}

object Interaction {

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = {
    val n = scala.math.pow(2.0, tile.zoom + 8)
    val lon_deg = tile.x / n * 360.0 - 180.0
    val lat_rad = scala.math.atan(scala.math.sinh(scala.math.Pi * (1 - 2 * tile.y / n)))
    val lat_deg = scala.math.toDegrees(lat_rad)
    Location(lat_deg, lon_deg)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param tile Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = {
    val pixels = (0 until (model.tileWidth * model.tileHeight)).par.map(pixel => {
      val color = Visualization.interpolateColor(colors,
                    Visualization.predictTemperature(temperatures,
                      tileLocation(Tile(
                        (pixel % model.tileWidth) / model.tileWidth + tile.x,
                        (pixel / model.tileWidth) / model.tileHeight + tile.y,
                        tile.zoom))))
      Pixel.apply(color.red, color.green, color.blue, model.tileAlpha)
    }).seq.toArray
    Image(model.tileWidth, model.tileHeight, pixels)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Year, Data)],
    generateImage: (Year, Tile, Data) => Unit
  ): Unit = {
    yearlyData.foreach(yd => {
      (0 to 3).foreach(z => {
        val maxPos = scala.math.pow(2, z).toInt
        (0 until maxPos).foreach(tx => {
          (0 until maxPos).foreach(ty => {
            generateImage(yd._1, Tile(tx, ty, z), yd._2)
          })
        })
      })
    })
  }
}