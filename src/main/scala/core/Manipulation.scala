package core

import model.{Location, Temperature, GridLocation}

object Manipulation {

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(temperatures: Iterable[(Location, Temperature)]): GridLocation => Temperature =
    gl => Visualization.predictTemperature(temperatures, Location(gl.lat, gl.lon))

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(temperaturess: Iterable[Iterable[(Location, Temperature)]]): GridLocation => Temperature =
    gl => temperaturess.map(gTs => makeGrid(gTs)(gl))
                       .foldLeft(0d)((curr, next) => curr + next) / temperaturess.size

  /**
    * @param temperatures Known temperatures
    * @param normals A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(temperatures: Iterable[(Location, Temperature)], normals: GridLocation => Temperature)
    : GridLocation => Temperature = 
      gl => makeGrid(temperatures)(gl) - normals(gl)
}