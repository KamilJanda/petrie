package agh.petrie.scraping.service

import scala.util.matching.Regex

class UrlRegexMatchingService {

  def matchRegex(regexes: List[Regex])(url: String) = {
    if(regexes.isEmpty) true else regexes.exists(r => r.findFirstIn(url).isDefined)

  }
}
