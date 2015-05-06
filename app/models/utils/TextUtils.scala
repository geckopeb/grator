package it.grator.utils

object TextUtils{
  /**
   * Takes a camel cased identifier name and returns an underscore separated name
   * Example:
   *     camelToUnderscores("thisIsA1Test") == "this_is_a_1_test"
   */
  def camelToUnderscores(name: String) = "[A-Z\\d]".r.replaceAllIn(name, {m =>
    "_" + m.group(0).toLowerCase()
  })
   
  /**
   * Takes an underscore separated identifier name and returns a camel cased one
   * Example:
   *    underscoreToCamel("this_is_a_1_test") == "thisIsA1Test"
   */
   
  def underscoreToCamel(name: String) = "_([a-z\\d])".r.replaceAllIn(name, {m =>
    m.group(1).toUpperCase()
  })

  def toOneLine(fields: List[String], separator: String): String = {
    fields.mkString(separator)
  }
}