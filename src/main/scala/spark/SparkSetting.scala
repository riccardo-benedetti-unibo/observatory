package spark

import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}

object SparkSetting {
  
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  implicit val sparkSession = SparkSession.builder()
                                          .master("spark://172.25.255.139:7077")
                                          //.master("local[*]")
                                          .appName("Observatory")
                                          .config("spark.executor.memory", "12G")
                                          .config("spark.driver.memory", "12G")
                                          .config("spark.network.timeout", "600s")
                                          .getOrCreate()
}