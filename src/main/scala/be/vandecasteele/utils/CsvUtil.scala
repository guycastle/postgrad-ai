package be.vandecasteele.utils

import java.nio.charset.Charset
import java.nio.file.Paths

import org.apache.commons.io.FileUtils

import scala.util.{Failure, Success, Try}

import collection.JavaConverters._

object CsvUtil {

  private final val DEFAULT_CSV_SEPARATOR = ","

  def readLines(filePath: String): Seq[String] = {
    Try {
      FileUtils.readLines(Paths.get(filePath).toFile, Charset.defaultCharset())
    } match {
      case Failure(exception) =>
        exception.printStackTrace()
        Seq.empty
      case Success(lines) => lines.asScala
    }
  }

  def hotEncodeRaggedCsv(fileToProcess: String, outputFilePath: String, csvSeparator: String = DEFAULT_CSV_SEPARATOR): Unit = {
    val lines = readLines(fileToProcess).filter(_.nonEmpty).map(ln => {
      val splitValues = ln.split(csvSeparator)
      splitValues(0) -> splitValues.slice(1, splitValues.size).filter(_.nonEmpty)
    })
    val statesHeaders = lines.flatMap(_._2).filter(_ != "gl").distinct.sorted
    val statesMap = statesHeaders.map(_ -> false).toMap
    val headers = Seq((Seq("name") ++ statesHeaders).mkString(csvSeparator))
    val newRows = lines.map(ln => {
      var lnHotEncoded = statesMap
      ln._2.foreach(s => {
        if (s == "gl") lnHotEncoded = lnHotEncoded + ("dengl" -> true)
        else lnHotEncoded = lnHotEncoded + (s -> true)
      })
      (Seq(ln._1) ++ statesHeaders.map(s => lnHotEncoded.getOrElse(s, false)).map(if (_) 1 else 0)).mkString(csvSeparator)
    })
    FileUtils.writeLines(
      Paths.get(outputFilePath).toFile,
      (headers ++ newRows).asJava,
      true
    )
  }
}
