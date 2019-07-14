package com.github.nvwa.cli4kotlin.option

/**
 * 字符串值选项
 *
 */
class StringOption : Option<String> {

    constructor(
            longForm: String,
            isRequired: Boolean,
            shortForm: Char? = null,
            helpDesc: String? = null
    ) : super (longForm, true, isRequired, shortForm, helpDesc)

    /**
     * 字符串选项不对字符串参数再进行解析，而是直接使用
     *
     * @param arg 字符串参数
     * @return 原始参数
     */
    override fun parse(arg: String): String = arg
}