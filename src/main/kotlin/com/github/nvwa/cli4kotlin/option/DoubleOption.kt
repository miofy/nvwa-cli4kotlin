package com.github.nvwa.cli4kotlin.option

import com.github.nvwa.cli4kotlin.exception.IllegalOptionValueException
import java.text.ParseException

/**
 * 双精度浮点数值选项
 */
class DoubleOption : Option<Double> {

  constructor(
          longForm: String,
          isRequired: Boolean,
          shortForm: Char? = null,
          helpDesc: String? = null
  ) : super(longForm, true, isRequired, shortForm, helpDesc)

  /**
   * 解析字符串参数成双精度浮点型选项值
   *
   * @param arg 字符串参数
   * @return 双精度浮点型选项值
   */
  @Throws(IllegalOptionValueException::class)
  override fun parse(arg: String): Double {
    try {
      return arg.toDouble()
    } catch (e: ParseException) {
      throw IllegalOptionValueException(this, arg)
    }
  }
}