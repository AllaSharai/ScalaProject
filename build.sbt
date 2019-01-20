name := "PlayWebsite"
 
version := "1.0" 
      
lazy val `playwebsite` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  evolutions,
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "org.postgresql" % "postgresql" % "42.2.4",
  "com.typesafe.play" %% "anorm" % "2.5.0")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      