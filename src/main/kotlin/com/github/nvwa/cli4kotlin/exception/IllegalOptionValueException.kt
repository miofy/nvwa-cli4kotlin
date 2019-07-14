package com.github.nvwa.cli4kotlin.exception

import com.github.nvwa.cli4kotlin.option.Option

/**
 * 非法选项值异常
 */
class IllegalOptionValueException(
        val option: Option<*>,
        value: String
) : OptionException("Illegal value '$value' for option '$option'")