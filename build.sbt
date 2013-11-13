name := "url-shortener"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.scalatest" %% "scalatest" % "2.0",
  "postgresql" % "postgresql" % "9.1-901.jdbc4"
)

play.Project.playScalaSettings ++ Seq(
  javaOptions in Test += "-Dconfig.file=conf/test.conf",
  testOptions in Test := Nil
)
