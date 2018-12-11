package test

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import spark.SparkSetting

@RunWith(classOf[JUnitRunner])
class SparkSettingSuite extends FunSuite {
  
  lazy val testObject = SparkSetting.sparkSession
  
  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a TimeUsage object")
  }
}