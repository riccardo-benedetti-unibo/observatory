package core

import model.{Color, Temperature, Layer, Year, LayerName}

object Interaction2 {

  object LayerName {
    case object Temperatures extends LayerName("temperatures")
    case object Deviations extends LayerName("deviations")
  }
  
  /**
    * @return The available layers of the application
    */
  def availableLayers: Seq[Layer] =
    Seq(Layer(LayerName.Temperatures, model.ColorTempsLegend, model.startYear to model.lastYear),
        Layer(LayerName.Deviations, model.DevTempsLegend, model.startYear to model.lastYear)
    )

  /**
    * @param selectedLayer A signal carrying the layer selected by the user
    * @return A signal containing the year bounds corresponding to the selected layer
    */
  def yearBounds(selectedLayer: Signal[Layer]): Signal[Range] = 
    Signal(selectedLayer().bounds)

  /**
    * @param selectedLayer The selected layer
    * @param sliderValue The value of the year slider
    * @return The value of the selected year, so that it never goes out of the layer bounds.
    *         If the value of `sliderValue` is out of the `selectedLayer` bounds,
    *         this method should return the closest value that is included
    *         in the `selectedLayer` bounds.
    */
  def yearSelection(selectedLayer: Signal[Layer], sliderValue: Signal[Year]): Signal[Year] =
    Signal(sliderValue() max yearBounds(selectedLayer)().min min yearBounds(selectedLayer)().max)

  /**
    * @param selectedLayer The selected layer
    * @param selectedYear The selected year
    * @return The URL pattern to retrieve tiles
    */
  def layerUrlPattern(selectedLayer: Signal[Layer], selectedYear: Signal[Year]): Signal[String] =
    Signal("generated/" + selectedLayer().layerName.id + "/" + selectedYear() + "/{z}/{x}/{y}.png")

  /**
    * @param selectedLayer The selected layer
    * @param selectedYear The selected year
    * @return The caption to show
    */
  def caption(selectedLayer: Signal[Layer], selectedYear: Signal[Year]): Signal[String] = 
    Signal(selectedLayer().layerName.id.capitalize + " (" + selectedYear() + ")")
}