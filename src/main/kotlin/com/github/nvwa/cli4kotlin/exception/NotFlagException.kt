package com.github.nvwa.cli4kotlin.exception

import com.github.nvwa.cli4kotlin.option.Option

/**
 * 无标志异常
 */
class NotFlagException(
    options: String,
    val option: Option<*>
) : UnknownOptionException(options,
    "Illegal option: -${option.shortForm} requires a value in '$option'")