

case class LogRecord(url: String, method: String, response: String)


def parseTomcatRecord(line: String) = {
    line.split(",") match {
      case (url, method, response) => LogRecord(url, method, response)
      case _ => LogRecord("", "", "")
    }
}

