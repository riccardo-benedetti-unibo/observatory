package test

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.apache.spark.sql.types.{StringType, IntegerType, DoubleType}
import core.CsvUtils

@RunWith(classOf[JUnitRunner])
class CsvUtilsTest extends FunSuite {
  
  val stations = CsvUtils.getStations("/test/stations.csv")
  val temperatures = CsvUtils.getTemperatures("/test/2015.csv")
  
  test("test getStations") {
    assert(stations.columns.length == 3)
    assert(stations.schema.toList.map(_.dataType) == List(StringType, DoubleType, DoubleType))
    assert(stations.count() == getCSVlines(CsvUtils.fsPath("/test/stations.csv")))
  }
  
  test("test getTemperatures") {
    assert(temperatures.columns.length == 4)
    assert(temperatures.schema.toList.map(_.dataType) == List(StringType, IntegerType, IntegerType, DoubleType))
    assert(temperatures.count() == getCSVlines(CsvUtils.fsPath("/test/2015.csv")))
  } 
  
  def getCSVlines(source: String): Int = {
    val bs = scala.io.Source.fromFile(source)
    bs.getLines().length
  }
}