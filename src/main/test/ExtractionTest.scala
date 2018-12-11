package test

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import java.time.LocalDate
import core.Extraction
import model.Location

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {

  val stationsFile = "/test/stations.csv"
  val temperaturesFile = "/test/2015.csv"
  val year = 2015
  
  val locTemps = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
  val locAvgTemps = Extraction.locationYearlyAverageRecords(locTemps)
  
  test("test locateTemperatures"){
    val expected = Seq(
        (LocalDate.of(2015, 8, 11), Location(37.35, -78.433), 27.3),
        (LocalDate.of(2015, 12, 6), Location(37.358, -78.438), 0.0),
        (LocalDate.of(2015, 1, 29), Location(37.358, -78.438), 2.0)
    )
  
    val locTempsRounded = locTemps.map(x => (x._1, x._2, BigDecimal(x._3).setScale(1, BigDecimal.RoundingMode.FLOOR).toDouble))
    
    assert(locTemps.size == expected.size)
    assert(locTempsRounded.toSet == expected.toSet)
  }
  
  test("test locationYearlyAverageRecords"){
    val expected = Seq(
        (Location(37.35, -78.433), 27.3),
        (Location(37.358, -78.438), 1.0)
    )
  
    val locAvgTempsRounded = locAvgTemps.map(x => (x._1, BigDecimal(x._2).setScale(1, BigDecimal.RoundingMode.FLOOR).toDouble))
    
    assert(locAvgTemps.size == expected.size)
    assert(locAvgTempsRounded.toSet == expected.toSet)
  }
}