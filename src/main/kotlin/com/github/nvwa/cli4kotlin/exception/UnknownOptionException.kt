package com.github.nvwa.cli4kotlin.exception

open class UnknownOptionException(
    private val optionLong: String,
    description: String = "Unknown option '$optionLong'"
) : OptionException(description)