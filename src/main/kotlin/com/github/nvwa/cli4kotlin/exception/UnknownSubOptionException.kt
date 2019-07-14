package com.github.nvwa.cli4kotlin.exception


class UnknownSubOptionException(
    options: String,
    subOption: Char
) : UnknownOptionException(options, "Unknown option: '$subOption' in '$options'")