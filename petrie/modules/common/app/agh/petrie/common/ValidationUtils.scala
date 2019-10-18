package agh.petrie.common

import cats.data.Validated._
object ValidationUtils {

  implicit class BooleanToValidated(boolean: Boolean) {
    def trueOrMessage(message: String) = {
      boolean match {
        case true  => Valid(())
        case false => Invalid(message)
      }
    }
  }
}
