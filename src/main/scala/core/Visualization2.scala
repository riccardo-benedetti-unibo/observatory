package core

import com.sksamuel.scrimage.{Image, Pixel}
import model.{CellPoint, Temperature, GridLocation, Color, Tile}

object Visualization2 {

  /**
    * @param point (x, y) coordinates of a point in the grid cell
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    point: CellPoint,
    d00: Temperature,
    d01: Temperature,
    d10: Temperature,
    d11: Temperature
  ): Temperature =
    d00 * (1 - point.x) * (1 - point.y) + d10 * point.x * (1 - point.y) +
      d01 * (1 - point.x) * point.y + d11 * point.x * point.y

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param tile Tile coordinates to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: GridLocation => Temperature,
    colors: Iterable[(Temperature, Color)],
    tile: Tile
  ): Image = {
    val pixels = (0 until (model.mapWidth * model.mapHeight)).par.map(pixel => {
      val tileLoc = Interaction.tileLocation(Tile(
            (pixel % model.mapWidth) / model.mapWidth + tile.x,
            (pixel / model.mapHeight) / model.mapHeight + tile.y,
            tile.zoom))
      val pixelX = tileLoc.lat
      val pixelY = tileLoc.lon
      val pixelTemp = bilinearInterpolation(
            CellPoint(pixelX, pixelY),
            grid(GridLocation(BigDecimal(pixelX).setScale(0, BigDecimal.RoundingMode.DOWN).toInt, 
                                BigDecimal(pixelY).setScale(0, BigDecimal.RoundingMode.DOWN).toInt)),
            grid(GridLocation(BigDecimal(pixelX).setScale(0, BigDecimal.RoundingMode.DOWN).toInt, 
                                BigDecimal(pixelY).setScale(0, BigDecimal.RoundingMode.UP).toInt)),
            grid(GridLocation(BigDecimal(pixelX).setScale(0, BigDecimal.RoundingMode.UP).toInt, 
                                BigDecimal(pixelY).setScale(0, BigDecimal.RoundingMode.DOWN).toInt)),
            grid(GridLocation(BigDecimal(pixelX).setScale(0, BigDecimal.RoundingMode.UP).toInt, 
                                BigDecimal(pixelY).setScale(0, BigDecimal.RoundingMode.UP).toInt)))
      val color = Visualization.interpolateColor(colors, pixelTemp)
      Pixel.apply(color.red, color.green, color.blue, model.mapAlpha)
    }).seq.toArray
    
    Image(model.mapWidth, model.mapHeight, pixels)
  }
}