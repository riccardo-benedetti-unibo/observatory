package test

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import core.{Extraction, Visualization}
import model.{Color, Location, Temperature}

@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers {
  
  val stationsFile = "/test/stations.csv"
  val temperaturesFile = "/test/2015.csv"
  val year = 2015
  
  val locTemps = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
  val locAvgTemps = Extraction.locationYearlyAverageRecords(locTemps)
  
  test("test toLocation") {
    assert(Visualization.toLocation(0) === Location(90, -180)) //left-up
    assert(Visualization.toLocation(180) === Location(90, 0)) //center-up
    assert(Visualization.toLocation(359) === Location(90, 179)) //right-up
    assert(Visualization.toLocation(360*90) === Location(0, -180)) //left-center
    assert(Visualization.toLocation(360*90 + 180) === Location(0, 0)) //center-center
    assert(Visualization.toLocation(360*90 + 180 + 179) === Location(0, 179)) //right-center
    assert(Visualization.toLocation(360*179) === Location(-89, -180)) //left-bottom
    assert(Visualization.toLocation(360*179 + 180) === Location(-89, 0)) //left-center
    assert(Visualization.toLocation(360*179 + 180 + 179) === Location(-89, 179)) //left-right
  }
  
  // correct values from: https://www.movable-type.co.uk/scripts/latlong.html
  // with a tolerance of 0.3 %
  test("test greatCircleDistExpanded") {
    val test1 = Visualization.greatCircleDistExpanded(Location(90,0), Location(0,0))
    val exp1 = 10010
    val test2 = Visualization.greatCircleDistExpanded(Location(20,20), Location(-40,7))
    val exp2 = 6807
    val test3 = Visualization.greatCircleDistExpanded(Location(89,-179), Location(-89,179))
    val exp3 = 19790
    assert((test1 > exp1 - (exp1 * 3 / 1000)) && (test1 < exp1 + (exp1 * 3 / 1000)))
    assert((test2 > exp2 - (exp2 * 3 / 1000)) && (test2 < exp2 + (exp2 * 3 / 1000)))
    assert((test3 > exp3 - (exp3 * 3 / 1000)) && (test3 < exp3 + (exp3 * 3 / 1000)))
  }
  
  test("test predictTemperature") {
    val testList: Iterable[(Location, Temperature)] = List(
          (Location(90,0), 100d),
          (Location(-90, 0), 0d)
        )
    val testLoc = Location(0,0)
    val testLoc2 = Location(90,0)
    val expTemp = 50f
    val expTemp2 = 100d
    
    assert(Visualization.predictTemperature(testList, testLoc) < expTemp + 0.001
        && Visualization.predictTemperature(testList, testLoc) > expTemp - 0.001)
    assert(Visualization.predictTemperature(testList, testLoc2) < expTemp2 + 0.001
        && Visualization.predictTemperature(testList, testLoc2) > expTemp2 - 0.001)
  }
  
  test("test interpolateColor") {
    val testList: Iterable[(Temperature, Color)] = List(
          (100d, Color(255, 255, 255)),
          (0d, Color(0, 0, 0))
        )
    val testTemp = 50d
    val expColor = Color(128, 128, 128)
    
    assert(Visualization.interpolateColor(testList, testTemp) === expColor)
  }
  
  test("test visualize") {
    val img = Visualization.visualize(locAvgTemps, model.ColorTempsLegend)
    img.output(new java.io.File("target/test.png"))
    assert(img.width == 360 && img.height == 180)
  }
}
