package agh.petrie.scraping.model

sealed trait SelectorType
case object XpathSelector extends SelectorType
case object CssSelector   extends SelectorType
