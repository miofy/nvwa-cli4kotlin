package com.github.nvwa.cli4kotlin.exception

import com.github.nvwa.cli4kotlin.option.Option

/**
 * 非法选项名异常
 */
class IllegalOptionNameException(
        val option: Option<*>
) : OptionException("Illegal name for option '$option'")