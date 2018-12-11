import sbt._

object Dependencies {
  //Version
  lazy val sparkSqlVersion = "2.4.0"
  lazy val scrimageCoreVersion = "2.1.8"
  lazy val jUnitVersion = "4.12"
  lazy val scalaCheckVersion = "1.14.0"
  lazy val scalaTestVersion = "3.0.1"
  
  // Libraries
  val sparkSql = "org.apache.spark" % "spark-sql_2.12" % sparkSqlVersion
  val scrimageCore = "com.sksamuel.scrimage" % "scrimage-core_2.12" % scrimageCoreVersion
  val jUnit = "junit" % "junit" % jUnitVersion
  val scalaCheck = "org.scalacheck" % "scalacheck_2.12" % scalaCheckVersion % "test"
  val scalaTest = "org.scalatest" % "scalatest_2.12" % scalaTestVersion % "test"
  
  // Projects
  val backendDeps =   Seq(
        sparkSql, 
        scrimageCore, 
        jUnit % Test, 
        scalaCheck,
        scalaTest)
  
}