package object model {
  type Temperature = Double
  type Year = Int
  
  final val startYear = 1985
  final val lastYear = 2015
  final val splitDevYear = 1990
  
  final val ColorTempsLegend = Seq(
        (60d, Color(255, 255, 255)),
        (32d, Color(255, 0, 0)),
        (12d, Color(255, 255, 0)),
        (0d, Color(0, 255, 255)),
        (-15d, Color(0, 0, 255)),
        (-27d, Color(255, 0, 255)),
        (-50d, Color(33, 0, 107)),
        (-60d, Color(0, 0, 0))
      )
      
  final val DevTempsLegend = Seq(
        (7d, Color(0, 0, 0)),
        (4d, Color(255, 0, 0)),
        (2d, Color(255, 255, 0)),
        (0d, Color(255, 255, 255)),
        (-2d, Color(0, 255, 255)),
        (-7d, Color(0, 0, 255))
      )
  
  final val mapWidth = 360
  final val mapHeight = 180
  final val mapAlpha = 255
  final val tileWidth = 256
  final val tileHeight = 256
  final val tileAlpha = 127
  
  //final val resourcesPath = "/main/scala/resources" 
  final val resourcesPath = "hdfs://172.25.255.139:8020/observatory/resources" 
}