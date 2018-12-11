package core

import java.time.LocalDate
import spark.SparkSetting.sparkSession
import model.{Year, Location, Temperature, LocateTemperaturesSchema}

object Extraction {
  
  import sparkSession.implicits._
  
  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): 
    Iterable[(LocalDate, Location, Temperature)] = {
    val stations = CsvUtils.getStations(stationsFile).persist()
    val temperatures = CsvUtils.getTemperatures(temperaturesFile).persist()
    val joined = stations.where(stations("latitude").isNotNull && stations("longitude").isNotNull)
                         .join(temperatures, "id")
                         .select(stations("latitude").as("latitude"), stations("longitude").as("longitude"), 
                                 temperatures("month").as("month"), temperatures("day").as("day"),
                                 temperatures("temperature").as("temperature"))
                         .as[LocateTemperaturesSchema]
    joined.collect().par.map(x => (LocalDate.of(year, x.month, x.day), 
                                  Location(x.latitude, x.longitude), 
                                  toCelsius(x.temperature))).seq
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    records.par.groupBy(_._2).mapValues(x => {
        x.foldLeft(0d)((t,next) => t + next._3) / x.size
      }).seq
  }
  
  def toCelsius(temp: Double): Double = {
    (temp - 32) / 9 * 5
  }
}