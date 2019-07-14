package com.github.nvwa.cli4kotlin.option

import com.github.nvwa.cli4kotlin.exception.IllegalOptionValueException

/**
 * 长整型值选项
 */
class LongOption : Option<Long> {

    constructor(longForm: String, isRequired: Boolean,
                shortForm: Char? = null, helpDesc: String? = null
    ) : super(longForm, true, isRequired, shortForm, helpDesc)

    /**
     * 解析字符串参数成长整型选项值
     *
     * @param arg 字符串参数
     * @return 长整型选项值
     */
    @Throws(IllegalOptionValueException::class)
    override fun parse(arg: String): Long {
        try {
            return arg.toLong()
        } catch (e: NumberFormatException) {
            throw IllegalOptionValueException(this, arg)
        }
    }
}