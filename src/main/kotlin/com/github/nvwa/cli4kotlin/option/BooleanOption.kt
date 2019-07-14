package com.github.nvwa.cli4kotlin.option

/**
 * 布尔值选项
 */
class BooleanOption : Option<Boolean> {

  constructor(
      longForm: String,
      isRequired: Boolean,
      shortForm: Char? = null,
      helpDesc: String? = null
  ) : super(longForm, false, isRequired, shortForm, helpDesc)

  /**
   * 只要有参数传入，就是解析成true返回
   *
   * @param arg 字符串参数
   * @return true
   */
  override fun parse(arg: String): Boolean = true
}