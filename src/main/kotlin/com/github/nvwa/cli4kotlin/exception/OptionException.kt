package com.github.nvwa.cli4kotlin.exception

/**
 * 选项异常抽象类
 */
abstract class OptionException(
    description: String
) : Exception(description)