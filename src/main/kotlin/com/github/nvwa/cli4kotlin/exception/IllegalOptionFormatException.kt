package com.github.nvwa.cli4kotlin.exception

/**
 * 非法选项格式异常
 */
class IllegalOptionFormatException(
        args: String
) : OptionException("Illegal option '$args'")