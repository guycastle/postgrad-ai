package be.vandecasteele

import be.vandecasteele.utils.CsvUtil

object App {

  def main(args: Array[String]) = {
    CsvUtil.hotEncodeRaggedCsv(
      fileToProcess = "/Users/guillaumevandecasteele/Projects/postgrad-ai/src/main/resources/plants.data",
      outputFilePath = "/Users/guillaumevandecasteele/Projects/postgrad-ai/src/main/resources/plants_hotencoded.data")
  }
}
