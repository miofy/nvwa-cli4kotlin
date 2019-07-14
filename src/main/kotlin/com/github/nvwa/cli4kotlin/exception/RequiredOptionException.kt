package com.github.nvwa.cli4kotlin.exception

import com.github.nvwa.cli4kotlin.option.Option

/**
 * 必须选项异常。若选项未在命令中显式指定，则抛出异常提醒。
 */
class RequiredOptionException(
    val option: Option<*>,
    description: String = "Option '$option' is required"
) : OptionException(description)