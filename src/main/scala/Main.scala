package observatory

import core.{Extraction, Visualization, Visualization2, Interaction, Manipulation}
import model.{Tile, Location, Temperature}
import java.time.LocalDate
import spark.SparkSetting

object Main extends App {

  lazy val sc = spark.SparkSetting.sparkSession.sparkContext
  val stationsFile = "/stations.csv"
  
  /*
   * GENERATE YEARLY TEMPERATURES MAPS
   */
  (model.startYear to model.lastYear).foreach(year => {
    if(!new java.io.File("/" + year + ".png").exists()) {
      val locTemps = Extraction.locateTemperatures(year, stationsFile, "/" + year + ".csv")
      val locAvgTemps = Extraction.locationYearlyAverageRecords(locTemps)
      val mapImg = Visualization.visualize(locAvgTemps, model.ColorTempsLegend)
      mapImg.output(new java.io.File("/" + year + ".png")) 
    }
  })
  
  /*
   * GENEATE TEMPERATURE TILES AT DIFFERENT ZOOM LEVELS
   */
  if(!new java.io.File("/temperatures").exists()) {
    new java.io.File("/temperatures").mkdir()
  }
  
  def generateImage(year: Int, tile: Tile, data: Iterable[(Location, Temperature)]): Unit = {
    if(!new java.io.File("/temperatures/" + year + "/" + tile.zoom + "/" + tile.x + "-" + tile.y + ".png").exists()) {
      new java.io.File("/temperatures/" + year + "/").mkdir()
      new java.io.File("/temperatures/" + year + "/" + tile.zoom + "/").mkdir()
      val tileImg = Interaction.tile(data, model.ColorTempsLegend, tile)
      tileImg.output(new java.io.File("/temperatures/" + year + "/" + tile.zoom + "/" + tile.x + "-" + tile.y + ".png")) 
    }
  }
  
  lazy val locAvgTemps = (model.startYear to model.lastYear)
                           .map(year => (year, Extraction.locationYearlyAverageRecords(
                                              Extraction.locateTemperatures(year, stationsFile, "/" + year + ".csv")))
  )
  
  Interaction.generateTiles(locAvgTemps, generateImage)
  
  /*
   * GENEATE DEVIATION TILES AT DIFFERENT ZOOM LEVELS
   */
  val normalGrid = Manipulation.average(locAvgTemps.filter(_._1 < model.splitDevYear).map(_._2))
  val deviations = locAvgTemps.filter(_._1 >= model.splitDevYear).map(
      data => (data._1, Manipulation.deviation(data._2, normalGrid)))
      
  if(!new java.io.File("/deviations").exists()) {
    new java.io.File("/deviations").mkdir()
  }
  
  deviations.foreach(yearlyDev => {
    new java.io.File("/deviations/" + yearlyDev._1 + "/").mkdir()
    (0 to 3).foreach(zoom => {
      val tiles = for {
        y <- 0 until scala.math.pow(2.0, zoom).toInt
        x <- 0 until scala.math.pow(2.0, zoom).toInt
      } yield Tile(x, y, zoom)
      new java.io.File("/deviations/" + yearlyDev._1 + "/" + zoom + "/").mkdir()
      tiles.foreach(t => {
        val devImg = Visualization2.visualizeGrid(yearlyDev._2, model.DevTempsLegend, t)
        devImg.output(new java.io.File("/deviations/" + yearlyDev._1 + "/" + t.zoom + "/" + t.x + "-" + t.y + ".png"))  
      })
    }) 
  })
}