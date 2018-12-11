package core

import java.nio.file.Paths
import org.apache.spark.sql.Dataset
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType}
import spark.SparkSetting._
import model.{StationSchema, TemperatureSchema}

object CsvUtils {
  
  import sparkSession.implicits._
  
  /** @return The filesystem path of the given resource */
  def fsPath(resource: String): String =
    Paths.get(getClass.getResource(model.resourcesPath + resource).toURI).toString
  
  def getStations(stationsPath: String): Dataset[StationSchema] =
    sparkSession.read
                .csv(fsPath(stationsPath))
                .select(concat_ws("&", coalesce('_c0, lit("")), '_c1).alias("id").cast(StringType), 
                        '_c2.alias("latitude").cast(DoubleType), 
                        '_c3.alias("longitude").cast(DoubleType))
                .as[StationSchema]
  
  def getTemperatures(temperaturesPath: String): Dataset[TemperatureSchema] =
    sparkSession.read
                .csv(fsPath(temperaturesPath))
                .select(concat_ws("&", coalesce('_c0, lit("")), '_c1).alias("id").cast(StringType), 
                        '_c2.alias("month").cast(IntegerType), 
                        '_c3.alias("day").cast(IntegerType), 
                        '_c4.alias("temperature").cast(DoubleType))
                .as[TemperatureSchema]
}