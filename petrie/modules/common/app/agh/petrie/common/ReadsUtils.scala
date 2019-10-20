package agh.petrie.common

object ReadsUtils {
  def extractClassName(c: Any): String = {
    val name = c.getClass.getName
    val parsed = if (name.last == '$') name.dropRight(1) else name
    parsed.split("\\.").last
  }
}
