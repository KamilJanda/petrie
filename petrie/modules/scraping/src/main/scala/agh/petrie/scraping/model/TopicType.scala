package agh.petrie.scraping.model

sealed trait TopicType
case object KeyWord extends TopicType
case object Sentence extends TopicType
