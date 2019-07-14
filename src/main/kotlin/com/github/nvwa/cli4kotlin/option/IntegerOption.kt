package com.github.nvwa.cli4kotlin.option

import com.github.nvwa.cli4kotlin.exception.IllegalOptionValueException

class IntegerOption : Option<Int> {

  constructor(
          longForm: String,
          isRequired: Boolean,
          shortForm: Char? = null,
          helpDesc: String? = null
  ) : super(longForm, true, isRequired, shortForm, helpDesc)

  @Throws(IllegalOptionValueException::class)
  override fun parse(arg: String): Int {
    try {
      return arg.toInt()
    } catch (e: NumberFormatException) {
      throw IllegalOptionValueException(this, arg)
    }
  }
}