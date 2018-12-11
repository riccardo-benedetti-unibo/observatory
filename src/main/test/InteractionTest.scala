package test

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import core.{Extraction, Interaction, CsvUtils}
import model.{Temperature, Color, Location, Tile}
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.nio.file.Paths

@RunWith(classOf[JUnitRunner])
class InteractionTest extends FunSuite {
  
  val stationsFile = "/test/stations.csv"
  val temperaturesFile = "/test/2015.csv"
  val year = 2015
  
  val locTemps = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
  val locAvgTemps = Extraction.locationYearlyAverageRecords(locTemps)
  
  test("test tileLocation") {
    assert(Interaction.tileLocation(Tile(0, 0, 0)).lat.round == 85 &&
        Interaction.tileLocation(Tile(0, 0, 0)).lon.round == -180)
    assert(Interaction.tileLocation(Tile(10, 10, 10)).lat.round == 85 &&
        Interaction.tileLocation(Tile(10, 10, 10)).lon.round == -180)
    assert(Interaction.tileLocation(Tile(5, 100, 100)).lat.round == 85 &&
        Interaction.tileLocation(Tile(5, 100, 100)).lon.round == -180)
  }
  
  test("test generateTiles") {
    def generateImage(year: Int, tile: Tile, data: Iterable[(Location, Double)]) = {
      val img = Interaction.tile(locAvgTemps, model.ColorTempsLegend, tile)
      img.output("/test.png")
    }
    val data: Set[(Int, Iterable[(Location, Double)])] = Set((2015, locAvgTemps))
    Interaction.generateTiles(data, generateImage)
    val generatedImg = ImageIO.read(new File("/test.png"))
    assert(generatedImg.getWidth() == 256 && generatedImg.getHeight() == 256)
    new File("/test.png").delete()
  }
}
